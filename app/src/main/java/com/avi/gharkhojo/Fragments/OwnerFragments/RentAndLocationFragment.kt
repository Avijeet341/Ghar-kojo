package com.avi.gharkhojo.Fragments.OwnerFragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.avi.gharkhojo.Model.PostDetails
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentRentAndLocationBinding
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RentAndLocationFragment : Fragment() {
    private var lat: Double = 0.0
    private var long: Double = 0.0
    private lateinit var checkBoxParkingIncluded: CheckBox
    private lateinit var textInputLayoutParkingCharges: TextInputLayout
    private lateinit var btnLiveLocation: Button
    private lateinit var btnTurnOnLocation: Button
    private lateinit var btnNext: Button
    private lateinit var textViewLongitude: TextView
    private lateinit var textViewLatitude: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var imageViewDeletePriceDetails: ImageView
    private lateinit var rent:TextInputEditText
    private lateinit var parkingCharges:TextInputEditText
    private lateinit var deposit:TextInputEditText
    private lateinit var pincode:TextInputEditText
    private lateinit var landmark:TextInputEditText
    private lateinit var houseNumber:TextInputEditText
    private lateinit var area:TextInputEditText
    private lateinit var colony:TextInputEditText
    private lateinit var state:TextInputEditText
    private lateinit var roadNo:TextInputEditText
    private lateinit var city:TextInputEditText
    private lateinit var maintenceCharges:TextInputEditText
    private var _binding: FragmentRentAndLocationBinding? = null
    private val binding get() = _binding!!




    companion object {
        private const val REQUEST_CHECK_SETTINGS = 1002
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentRentAndLocationBinding.inflate(inflater, container, false)

        initializeViews()
        setupLocationServices()
        setupAnimations()
        setupListeners()

        return binding.root
    }


    private fun initializeViews() {
        checkBoxParkingIncluded = binding.checkBoxParkingIncluded
        textInputLayoutParkingCharges = binding.textInputLayoutParkingCharges
        btnLiveLocation = binding.btnLiveLocation
        btnTurnOnLocation = binding.btnTurnOnLocation
        btnNext = binding.btnNext
        textViewLongitude = binding.textViewLongitude
        textViewLatitude = binding.textViewLatitude
        imageViewDeletePriceDetails = binding.imageViewDeletePriceDetails
        textViewLongitude.visibility = View.GONE
        textViewLatitude.visibility = View.GONE
        rent = binding.autoCompleteTextViewRent
        parkingCharges = binding.autoCompleteTextViewParkingCharges
        deposit = binding.autoCompleteTextViewDeposit
        pincode = binding.autoCompleteTextViewPincode
        landmark = binding.autoCompleteTextViewLandmark
        houseNumber = binding.autoCompleteTextViewHouseNumber
        area = binding.autoCompleteTextViewArea
        colony = binding.autoCompleteTextViewColony
        city = binding.autoCompleteTextViewCity
        maintenceCharges = binding.maintenanceCharge
        state = binding.autoCompleteTextViewState
        roadNo = binding.autoCompleteTextViewRoadLaneNumber
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.checkBoxSameAsCurrentAddress.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
            pincode.setText(UserData.Pincode)
                landmark.setText(UserData.LandMark)
            houseNumber.setText(UserData.HouseNo.toString())
            area.setText(UserData.Area)
            colony.setText(UserData.colony)
            city.setText(UserData.City)
                state.setText(UserData.State)
                roadNo.setText(UserData.Road_Lane)


            }
            else{
                pincode.setText("")
                landmark.setText("")
                houseNumber.setText("")
                area.setText("")
                colony.setText("")
                city.setText("")
                state.setText("")
                roadNo.setText("")
            }
        }

    }


    private fun setupLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }
    private fun setupAnimations() {
        val cardViews = listOf<CardView>(
            binding.cardViewPriceDetails,
            binding.cardViewLocationDetails,
            binding.cardViewPropertyDescription
        )

        val animatorSet = AnimatorSet()
        val animators = cardViews.mapIndexed { index, cardView ->
            ObjectAnimator.ofFloat(cardView, "translationY", 1000f, 0f).apply {
                duration = 500
                startDelay = index * 100L
            }
        }

        animatorSet.playTogether(*animators.toTypedArray())
        animatorSet.start()
    }
    private fun setupListeners() {
        checkBoxParkingIncluded.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                textInputLayoutParkingCharges.visibility = View.GONE
                parkingCharges?.setText("")
            } else {
                textInputLayoutParkingCharges.visibility = View.VISIBLE
                textInputLayoutParkingCharges.alpha = 0f
                textInputLayoutParkingCharges.animate().alpha(1f).duration = 300
            }
        }

        btnNext.setOnClickListener {
            it.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction {
                    it.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
        }

        setupTextInputAnimations()

        btnTurnOnLocation.setOnClickListener {
            checkLocationPermission()
        }

        btnLiveLocation.setOnClickListener {
            btnLiveLocation.isEnabled = false
            lifecycleScope.launch {
                getLocation()
                delayButton(btnLiveLocation, 3000)
            }
        }

        imageViewDeletePriceDetails.setOnClickListener {
            clearStoredData()
        }
        btnNext.setOnClickListener {
            if(!isAllFieldsFilled()){
                return@setOnClickListener
        }
            with(PostDetails){
                this.parkingCharge = if(checkBoxParkingIncluded.isChecked) 0.0 else parkingCharges.text.toString().trim().toDouble()
                this.rent = binding.autoCompleteTextViewRent.text.toString().trim().toDouble()
                this.deposit = binding.autoCompleteTextViewDeposit.text.toString().trim().toDouble()
                this.pincode = binding.autoCompleteTextViewPincode.text.toString().trim().toInt()
                this.landMark = binding.autoCompleteTextViewLandmark.text.toString().trim()
                this.houseNumber = binding.autoCompleteTextViewHouseNumber.text.toString().trim().toInt()
                this.area = binding.autoCompleteTextViewArea.text.toString().trim()
                this.colony = binding.autoCompleteTextViewColony.text.toString().trim()
                this.city = binding.autoCompleteTextViewCity.text.toString().trim()
                this.isParkingChargeIncluded = binding.checkBoxParkingIncluded.isChecked
                this.description = binding.textInputLayoutPropertyDescription.editText?.text.toString().trim()
                this.latitude = lat
                this.longitude = long
                this.maintenanceCharge = binding.maintenanceCharge.text.toString().trim().toDouble()
                this.state = this@RentAndLocationFragment.state.text.toString().trim()


            }
            findNavController().navigate(R.id.action_rentAndLocationFragment_to_roomPhotosFragment)
        }
    }
    private fun setupTextInputAnimations() {
        val autoCompleteTextViews = listOf(
            R.id.autoCompleteTextViewDeposit, R.id.autoCompleteTextViewPincode,
            R.id.autoCompleteTextViewLandmark, R.id.autoCompleteTextViewHouseNumber,
            R.id.autoCompleteTextViewArea, R.id.autoCompleteTextViewColony,
            R.id.autoCompleteTextViewCity
        )

        autoCompleteTextViews.forEach { autoCompleteTextViewId ->
            view?.findViewById<AutoCompleteTextView>(autoCompleteTextViewId)?.setOnFocusChangeListener { v, hasFocus ->
                v.animate().scaleX(if (hasFocus) 1.05f else 1f).scaleY(if (hasFocus) 1.05f else 1f).setDuration(200).start()
            }
        }

       binding.textInputLayoutPropertyDescription
            .editText?.setOnFocusChangeListener { v, hasFocus ->
                v.animate().scaleX(if (hasFocus) 1.05f else 1f).scaleY(if (hasFocus) 1.05f else 1f).setDuration(200).start()
            }
    }


    private fun clearStoredData() {


       binding.textInputLayoutPropertyDescription.editText?.setText("")
        binding.checkBoxParkingIncluded.isChecked = false
        parkingCharges?.setText("")
        textInputLayoutParkingCharges.visibility = View.GONE
        rent.setText("")
        parkingCharges.setText("")
        deposit.setText("")
        pincode.setText("")
        landmark.setText("")
        houseNumber.setText("")
        area.setText("")
        colony.setText("")
        city.setText("")
        btnLiveLocation.isEnabled = true
        btnTurnOnLocation.isEnabled = true
        btnNext.isEnabled = true
        textViewLongitude.visibility = View.GONE
        textViewLatitude.visibility = View.GONE
        textViewLongitude.text = ""
        textViewLatitude.text = ""
        maintenceCharges.setText("")
        Toast.makeText(context, "All data has been cleared", Toast.LENGTH_SHORT).show()


    }

    private suspend fun getLocation() {
        if (!isLocationEnabled()) {
            promptUserToEnableLocationSettings()
            return
        }

        when {
            hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) -> fetchLocation(Priority.PRIORITY_HIGH_ACCURACY)
            hasPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) -> fetchLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY)
            else -> requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private suspend fun fetchLocation(priority: Int) {
        try {
            if (!hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
                !hasPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
                return
            }

            val locationResult = fusedLocationClient.getCurrentLocation(priority, null).await()

            locationResult?.let {
                updateUIWithLocation(it.latitude, it.longitude)
            } ?: run {
                Toast.makeText(context, "Failed to obtain location", Toast.LENGTH_LONG).show()
            }
        } catch (e: SecurityException) {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error getting location: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            btnLiveLocation.isEnabled = true
        }
    }


    private fun updateUIWithLocation(latitude: Double, longitude: Double) {
        textViewLongitude.text = "Longitude: $longitude"
        textViewLatitude.text = "Latitude: $latitude"
        lat = latitude
        long = longitude
        textViewLongitude.visibility = View.VISIBLE
        textViewLatitude.visibility = View.VISIBLE
        Toast.makeText(context, "Location obtained successfully", Toast.LENGTH_SHORT).show()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun promptUserToEnableLocationSettings() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
        }.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK) {
            lifecycleScope.launch {
                getLocation()
                delayButton(btnLiveLocation, 3000)
            }
        } else {
            Toast.makeText(context, "Please enable location services", Toast.LENGTH_LONG).show()
            btnLiveLocation.isEnabled = true
        }
    }



    private fun checkLocationPermission() {
        when {
            hasPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(context, "Precise location permission is already enabled", Toast.LENGTH_SHORT).show()
            }
            hasPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) -> {
                Toast.makeText(context, "Approximate location permission is already enabled", Toast.LENGTH_SHORT).show()
            }
            else -> {
                requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }
    private fun isAllFieldsFilled(): Boolean {

        if(
            rent.text.toString().trim().isNotEmpty()
            && deposit.text.toString().trim().isNotEmpty() && pincode.text.toString().trim().isNotEmpty()
            && landmark.text.toString().trim().isNotEmpty() && houseNumber.text.toString().trim().isNotEmpty()
            && area.text.toString().trim().isNotEmpty() && colony.text.toString().trim().isNotEmpty()
            && city.text.toString().trim().isNotEmpty()
            && (binding.checkBoxParkingIncluded.isChecked || parkingCharges.text.toString().trim().isNotEmpty())
            && maintenceCharges.text.toString().trim().isNotEmpty()
            && state.text.toString().trim().isNotEmpty()
            && roadNo.text.toString().trim().isNotEmpty()

        ){
            return true
        }
            Toast.makeText(context, "Please fill all  stared fields", Toast.LENGTH_SHORT).show()
            return false

    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }

    private suspend fun delayButton(button: Button, delay: Long) {
        button.isEnabled = false
        kotlinx.coroutines.delay(delay)
        button.isEnabled = true
    }
}
