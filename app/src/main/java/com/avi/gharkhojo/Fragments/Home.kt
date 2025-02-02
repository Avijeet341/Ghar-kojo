package com.avi.gharkhojo.Fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Adapter.GridAdapter
import com.avi.gharkhojo.Adapter.HousingTypeAdapter
import com.avi.gharkhojo.Model.HousingType
import com.avi.gharkhojo.Model.Post
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.OwnerActivity
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentHomeBinding
import com.bumptech.glide.RequestManager
import com.google.android.gms.location.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var filterAnimation: android.view.animation.Animation

    @Inject
    lateinit var requestManager: RequestManager

    private var databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("Posts")
    private lateinit var gridAdapter: GridAdapter

    // Location related properties
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 10000 // Update interval in milliseconds
        fastestInterval = 5000
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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
        setupLocation()
        observeDataChanges()
    }

    private fun setupLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    updateLocationUI(location.latitude, location.longitude)
                }
            }
        }

        requestLocationUpdates()
    }

    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
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

    private fun updateLocationUI(latitude: Double, longitude: Double) {
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            addresses?.firstOrNull()?.let { address ->
                // Try to get the subLocality (neighborhood) first
                val locality = address.subLocality ?:
                address.locality ?:
                address.subAdminArea

                // Update the UI on the main thread
                activity?.runOnUiThread {
                    binding.locationText1.text = locality
                    binding.locationText3.text = address.locality ?: address.adminArea
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
        val searchView =
            binding.toolbar.findViewById<SearchView>(R.id.search_view)
        val searchText =
            searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchText.setTextColor(ContextCompat.getColor(requireContext(), R.color.expBlue))
        searchText.setHintTextColor(
            ContextCompat.getColor(requireContext(), R.color.expBlue)
        )
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
        val recyclerView: RecyclerView =
            binding.toolbar.findViewById(R.id.housingTypeRecyclerView)
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
            val bundle = Bundle().apply {
                putParcelable("post", post)
            }
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
                    val mutableList: MutableList<Post> = mutableListOf()
                    for (dataSnapshot in snapshot.children) {
                        for (data in dataSnapshot.children) {
                            val post = data.getValue(Post::class.java)
                            if (post != null) {
                                mutableList.add(post)
                            }
                        }
                    }
                    gridAdapter.updateData(mutableList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (_binding == null) return
                Toast.makeText(
                    context,
                    "Failed to load data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationUpdates()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}