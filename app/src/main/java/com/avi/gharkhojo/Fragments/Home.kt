package com.avi.gharkhojo.Fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.avi.gharkhojo.Adapter.GridAdapter
import com.avi.gharkhojo.Adapter.HousingTypeAdapter
import com.avi.gharkhojo.Model.HousingType
import com.avi.gharkhojo.Model.Post
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.OwnerActivity
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentHomeBinding
import com.bumptech.glide.RequestManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@AndroidEntryPoint
class Home : Fragment() {

    // View Binding.
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var filterAnimation: Animation

    @javax.inject.Inject
    lateinit var requestManager: RequestManager

    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("Posts")
    private lateinit var gridAdapter: GridAdapter

    // Location-related properties.
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    // Create a location request for high accuracy.
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 10_000     // 10 seconds.
        fastestInterval = 5_000 // 5 seconds.
    }

    companion object {
        // Cached location display strings and coordinates.
        var savedLocality: String? = null   // For binding.locationText1.
        var savedCity: String? = null         // For binding.locationText3.
        var savedLatitude: Double? = null
        var savedLongitude: Double? = null

        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001

        // Clear cached values if needed.
        fun clearSavedLocation() {
            savedLocality = null
            savedCity = null
            savedLatitude = null
            savedLongitude = null
        }
    }

    // Using the new Activity Result API for resolving location settings.
    private val locationAccuracyLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // User accepted location settings—request a location update.
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserProfile()
        setupToolbar()
        setupGridView()
        setupSearchView()
        setupFilterButtonAnimation()

        // If cached location exists, update UI immediately.
        if (savedLocality != null && savedCity != null) {
            binding.locationText1.text = savedLocality
            binding.locationText3.text = savedCity
        } else {
            // Attempt to fetch location immediately.
            getLastKnownLocationOrRequestSettings()
        }

        observeDataChanges()
    }

    /**
     * Attempts to get the last-known location. If unavailable (which can happen the very first time),
     * uses getCurrentLocation() to obtain a fresh location. If both fail, falls back to checking location settings.
     */
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

        // Try last-known location first.
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                geocodeLocation(location)
            } else {
                // If last-known location is null, use getCurrentLocation().
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY, null
                ).addOnSuccessListener { currentLocation: Location? ->
                    if (currentLocation != null) {
                        geocodeLocation(currentLocation)
                    } else {
                        // If still null, request location settings to trigger a fresh update.
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

    /**
     * Checks the device's location settings. If high-accuracy is not enabled, shows the system
     * "Location Accuracy" dialog using the new Activity Result API.
     */
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

    /**
     * Requests a location update. In the callback, performs geocoding asynchronously.
     */
    private fun requestLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    geocodeLocation(location)
                    // Since we need only one fix per session, stop further updates.
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

    /**
     * Performs geocoding for the provided location off the main thread.
     * Once complete, caches the result, updates the UI, and shows a toast.
     */
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

                    // Update UI.
                    binding.locationText1.text = savedLocality
                    binding.locationText3.text = savedCity

                    // Show a toast after the location is fetched.
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
        filterAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.filter_button_animation)
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
    }

    private fun setupUserProfile() {
        UserData.profilePictureUrl?.let { url ->
            requestManager.load(url).placeholder(R.drawable.vibe).into(binding.userImage)
        } ?: run {
            binding.userImage.setImageResource(R.drawable.vibe)
        }
        binding.username.text =
            UserData.username ?: getString(R.string.default_username)
    }

    private fun setupToolbar() {
        val recyclerView = binding.toolbar.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.housingTypeRecyclerView)
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
        val adapter = HousingTypeAdapter(housingTypes) {
            val intent = Intent(requireContext(), OwnerActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
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

    private fun observeDataChanges() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (_binding == null) return
                if (snapshot.exists()) {
                    val mutableList = mutableListOf<Post>()
                    for (dataSnapshot in snapshot.children) {
                        for (data in dataSnapshot.children) {
                            data.getValue(Post::class.java)?.let { mutableList.add(it) }
                        }
                    }
                    gridAdapter.updateData(mutableList)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                if (_binding == null) return
                Toast.makeText(
                    requireContext(),
                    "Failed to load data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
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
