package com.avi.gharkhojo.Fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.avi.gharkhojo.Adapter.GridAdapter
import com.avi.gharkhojo.Adapter.HousingTypeAdapter
import com.avi.gharkhojo.Model.DataSharing
import com.avi.gharkhojo.Model.HousingType
import com.avi.gharkhojo.Model.Post
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.OwnerActivity
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentHomeBinding
import com.bumptech.glide.RequestManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@AndroidEntryPoint
class Home : Fragment() {

    private var searchedText: String  = ""
    private var sortOption: String? = null
    private val dataSharing: DataSharing by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var areasList:ArrayList<String> = ArrayList();
    private lateinit var filterAnimation: Animation
    private var filterPost: Post? = null
    private var mutableList: MutableList<Post> = mutableListOf()
    @javax.inject.Inject
    lateinit var requestManager: RequestManager

    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("Posts")
    private lateinit var gridAdapter: GridAdapter


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback


    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 10_000     // 10 seconds.
        fastestInterval = 5_000 // 5 seconds.
    }

    companion object {

        var savedLocality: String? = null
        var savedCity: String? = null
        var savedLatitude: Double? = null
        var savedLongitude: Double? = null

        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001


        fun clearSavedLocation() {
            savedLocality = null
            savedCity = null
            savedLatitude = null
            savedLongitude = null
        }
    }

    private val locationAccuracyLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                requestLocationUpdates()
            } else {
                Toast.makeText(
                    requireContext(),
                    "High accuracy location is recommended for best experience",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        activity?.findViewById<ChipNavigationBar>(R.id.bottom_nav_bar)?.visibility = View.VISIBLE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserProfile()
        setupToolbar()
        setupGridView()
        setupSearchView()
        setupFilterButtonAnimation()


        if (savedLocality != null && savedCity != null) {
            binding.locationText1.text = savedLocality
            binding.locationText3.text = savedCity
        } else {

            getLastKnownLocationOrRequestSettings()
        }

        observeDataChanges()
        parentFragmentManager.setFragmentResultListener("filterResult", this) { _, result ->
            filterPost = result.getParcelable<Post>("filterPost")
            sortOption = result.getString("sortOption")
            areasList = result.getStringArrayList("areas")?: ArrayList()

            observeDataChanges()
        }
    }


    private fun getLastKnownLocationOrRequestSettings() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }


        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                geocodeLocation(location)
            } else {

                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY, null
                ).addOnSuccessListener { currentLocation: Location? ->
                    if (currentLocation != null) {
                        geocodeLocation(currentLocation)
                    } else {

                        requestLocationSettings()
                    }
                }.addOnFailureListener {
                    requestLocationSettings()
                }
            }
        }.addOnFailureListener {
            requestLocationSettings()
        }
    }


    private fun requestLocationSettings() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
        val settingsClient: SettingsClient =
            LocationServices.getSettingsClient(requireActivity())
        val task = settingsClient.checkLocationSettings(builder.build())
        task.addOnSuccessListener {
            // All settings are satisfied; request location updates.
            requestLocationUpdates()
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    locationAccuracyLauncher.launch(intentSenderRequest)
                } catch (sendEx: Exception) {
                    sendEx.printStackTrace()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Location settings are inadequate.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    private fun requestLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    geocodeLocation(location)

                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }


    private fun geocodeLocation(location: Location) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = withContext(Dispatchers.IO) {
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)
                }
                val address = addresses?.firstOrNull()
                if (address != null) {
                    savedLocality = address.subLocality
                        ?: address.locality
                                ?: address.subAdminArea ?: "Unknown"
                    savedCity = address.locality ?: address.adminArea ?: "Unknown"
                    savedLatitude = location.latitude
                    savedLongitude = location.longitude


                    binding.locationText1.text = savedLocality
                    binding.locationText3.text = savedCity


                    Toast.makeText(requireContext(), "Location fetched", Toast.LENGTH_SHORT).show()
                } else {
                    binding.locationText1.text = "N/A"
                    binding.locationText3.text = "N/A"
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setupFilterButtonAnimation() {
        filterAnimation =
            AnimationUtils.loadAnimation(requireContext(), R.anim.filter_button_animation)
        binding.filterButton.setOnClickListener {
            it.startAnimation(filterAnimation)
            findNavController().navigate(R.id.action_home2_to_filterFragment)
        }
    }

    private fun setupSearchView() {
        val searchView = binding.toolbar.findViewById<SearchView>(R.id.search_view)
        val searchText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchText.setTextColor(requireContext().getColor(R.color.expBlue))
        searchText.setHintTextColor(requireContext().getColor(R.color.expBlue))

        searchText.doOnTextChanged({ text, _, _, _ ->
            dataSharing.searchedText.value = text.toString().trim()
            onSearch(dataSharing.searchedText.value)
        })
    }

    private fun setupUserProfile() {
        UserData.profilePictureUrl?.let { url ->
            requestManager.load(url).placeholder(R.drawable.vibe).into(binding.userImage)
        } ?: run {
            binding.userImage.setImageResource(R.drawable.vibe)
        }
        binding.username.text =
            UserData.username ?: getString(R.string.default_username)

        binding.userImage.setOnClickListener {
            findNavController().navigate(R.id.action_home2_to_profile)
            val bottomNav = activity?.findViewById<ChipNavigationBar>(R.id.bottom_nav_bar)
            bottomNav?.setItemSelected(R.id.nav_profile, true)
        }
    }

    private fun setupToolbar() {
        val recyclerView =
            binding.toolbar.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.housingTypeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
        val housingTypes = listOf(
            HousingType(R.drawable.ic_baseline_add_24, "Add Property"),
            HousingType(R.drawable.home, "House"),
            HousingType(R.drawable.apartment, "Apartment"),
            HousingType(R.drawable.building, "Flat"),
            HousingType(R.drawable.dormitory, "Dormitory"),
            HousingType(R.drawable.luxury, "Luxury"),
            HousingType(R.drawable.commercial_property, "Commercial")
        )
        val adapter = HousingTypeAdapter(
            housingTypes, {
                val intent = Intent(requireContext(), OwnerActivity::class.java)
                startActivity(intent)
                this.requireActivity().finish()
            },
            { housingType ->
                observeDataChanges(housingType)
            })
        recyclerView.adapter = adapter
    }

    private fun setupGridView() {
        gridAdapter = GridAdapter { post ->
            val action = HomeDirections.actionHome2ToHomeDetails()
            val bundle = Bundle().apply { putParcelable("post", post) }
            action.arguments.putAll(bundle)
            findNavController().navigate(action)
        }
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = gridAdapter
        }
    }

    private fun observeDataChanges(filter: MutableList<String>? = null) {
        Log.d("h", filter.toString())
        mutableList.clear()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Null check before accessing binding
                if (_binding == null) {
                    return
                }
                if (snapshot.exists()) {

                    for (dataSnapshot in snapshot.children) {
                        for (data in dataSnapshot.children) {
                            val post = data.getValue(Post::class.java)
                            if (post != null) {
                                if (filter != null) {
                                    filterPost = null
                                    if (filter.contains(post.propertyType)) {
                                        mutableList.add(post)
                                    }
                                }
                                if(filter.isNullOrEmpty()){
                                    if(filterPost!=null){


                                        var postBuiltUpArea = currencyToFloat(post.builtUpArea!!)
                                        var filterFloorPosition = filterPost!!.floorPosition?.toInt()
                                        var postRent = post.rent?.let { currencyToFloat(it) }
                                        var filterBudget:List<Float> = filterPost!!.rent.toString().split("-").map { currencyToFloat(it) }
                                        var filterBuiltUpArea:List<Float> = filterPost!!.builtUpArea.toString().split("-").map { currencyToFloat(it) }
                                        var postFloorPosition = post.floorPosition?.toInt()

                                        if(((postRent!! in (filterBudget[0]..filterBudget[1])) ||
                                                    ((filterBudget[1].toInt() > 50000) && (postRent > filterBudget[1]))) &&
                                            ((postBuiltUpArea in (filterBuiltUpArea[0]..filterBuiltUpArea[1]))
                                                    || ((filterBuiltUpArea[1].toInt() > 3000) && (postBuiltUpArea > filterBuiltUpArea[1])))
                                            && ((postFloorPosition!! == filterFloorPosition!!)
                                                    || (filterFloorPosition == -1))
                                            && ((post.preferredTenants == filterPost?.preferredTenants)
                                                    || (filterPost?.preferredTenants == "Any"))
                                            && ((post.propertyType == filterPost?.propertyType) || (filterPost?.propertyType == "Any"))
                                            && ((post.noOfBedRoom == filterPost?.noOfBedRoom) || (filterPost?.noOfBedRoom == -1))
                                            && ((post.noOfBathroom == filterPost?.noOfBathroom) || (filterPost?.noOfBathroom == -1))
                                            && ((post.noOfBalcony == filterPost?.noOfBalcony) || (filterPost?.noOfBalcony == -1))
                                            && ((post.noOfKitchen == filterPost?.noOfKitchen) || (filterPost?.noOfKitchen == -1))
                                            && ((filterPost?.hasLift == null) || ((filterPost?.hasLift == true) && (post.hasLift == true)))
                                            && ((filterPost?.hasGenerator == null) || ((filterPost?.hasGenerator == true) && (post.hasGenerator == true)))
                                            && ((filterPost?.hasGasService == null) || ((filterPost?.hasGasService == true) && (post.hasGasService == true)))
                                            && ((filterPost?.hasSecurityGuard == null) || ((filterPost?.hasSecurityGuard == true) && (post.hasSecurityGuard == true)))
                                            && ((filterPost?.hasParking == null) || ((filterPost?.hasParking == true) && (post.hasParking == true)))
                                            && (areasList.isEmpty() || areasList.contains(post.area!!.uppercase()))
                                        ){

//                                                Log.d("postRent", postRent.toString())
                                            if(!mutableList.contains(post)){
                                                mutableList.add(post)
                                            }
                                        }
                                    }else{
                                        mutableList.add(post)
                                    }

                                    if(!sortOption.isNullOrEmpty() && sortOption == "l-h"){
                                        mutableList.sortBy { currencyToFloat(it.rent!!) }
                                    }
                                    if(!sortOption.isNullOrEmpty() && sortOption == "h-l"){
                                        mutableList.sortByDescending { currencyToFloat(it.rent!!) }
                                    }

                                }


                            }
                        }
                    }
                    (binding.recyclerView.adapter as? GridAdapter)?.updateData(mutableList)

                    if(dataSharing.searchedText.value?.isNotEmpty() == true){

                        binding.searchView.setQuery(dataSharing.searchedText.value, false)
                        onSearch(dataSharing.searchedText.value)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Null check before accessing binding
                if (_binding == null) {
                    return
                }
                Toast.makeText(context, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun onSearch(search:String?) {



        if(search.isNullOrEmpty()){
            mutableList.clear()
            dataSharing.searchedData.value?.clear()
            observeDataChanges()
        }
        else{

            if(mutableList.isNotEmpty()){

                var searchList: MutableList<Post> = mutableListOf()
                mutableList.forEach{
                    if((it.city!!.contains(search, ignoreCase = true)
                                || it.area!!.contains(search, ignoreCase = true)
                                || it.propertyType!!.contains(search, ignoreCase = true)
                                || it.preferredTenants!!.contains(search, ignoreCase = true)
                                || it.furnished!!.contains(search, ignoreCase = true)
                                || it.colony!!.contains(search,ignoreCase = true)
                                || it.state!!.contains(search, ignoreCase = true)
                                || "${it.noOfBedRoom} BHK ${it.propertyType}".contains(search, ignoreCase = true)) &&
                        !searchList.contains(it)){

                        searchList.add(it)

                    }else{
                        searchList.remove(it)
                    }
                }
                dataSharing.searchedData = MutableLiveData(searchList)
                dataSharing.searchedData.value?.let {
                    (binding.recyclerView.adapter as? GridAdapter)?.updateData(
                        it
                    )
                }

            }
        }
    }
    fun currencyToFloat(currencyString: String): Float {
        val cleanString = currencyString.replace("[^\\d.]".toRegex(), "")
        return cleanString.toFloat()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                getLastKnownLocationOrRequestSettings()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        _binding = null
    }
}


