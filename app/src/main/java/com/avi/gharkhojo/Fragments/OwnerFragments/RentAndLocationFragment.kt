package com.avi.gharkhojo.Fragments.OwnerFragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.avi.gharkhojo.R
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.textfield.TextInputLayout

class RentAndLocationFragment : Fragment() {

    private lateinit var checkBoxParkingIncluded: CheckBox
    private lateinit var textInputLayoutParkingCharges: TextInputLayout
    private lateinit var btnLiveLocation: Button
    private lateinit var textViewLongitude: TextView
    private lateinit var textViewLatitude: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationSettingsRequest: LocationSettingsRequest

    companion object {
        private const val REQUEST_CHECK_SETTINGS = 1002
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_rent_and_location, container, false)

        checkBoxParkingIncluded = view.findViewById(R.id.checkBoxParkingIncluded)
        textInputLayoutParkingCharges = view.findViewById(R.id.textInputLayoutParkingCharges)
        btnLiveLocation = view.findViewById(R.id.btnLiveLocation)
        textViewLongitude = view.findViewById(R.id.textViewLongitude)
        textViewLatitude = view.findViewById(R.id.textViewLatitude)

        textViewLongitude.visibility = View.GONE
        textViewLatitude.visibility = View.GONE

        sharedPreferences = requireActivity().getSharedPreferences("RentLocationPrefs", Context.MODE_PRIVATE)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setupLocationRequest()
        setupAnimations(view)
        setupListeners()
        loadSavedData()

        return view
    }

    private fun setupLocationRequest() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(10000)
            .build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true) // Show the location settings dialog

        locationSettingsRequest = builder.build()
    }

    private fun setupAnimations(view: View) {
        val cardViews = listOf<CardView>(
            view.findViewById(R.id.cardViewPriceDetails),
            view.findViewById(R.id.cardViewLocationDetails),
            view.findViewById(R.id.cardViewPropertyDescription)
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
            } else {
                textInputLayoutParkingCharges.visibility = View.VISIBLE
                textInputLayoutParkingCharges.alpha = 0f
                textInputLayoutParkingCharges.animate().alpha(1f).duration = 300
            }
        }

        view?.findViewById<View>(R.id.btnNext)?.setOnClickListener {
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

        val autoCompleteTextViews = listOf(
            R.id.autoCompleteTextViewRent, R.id.autoCompleteTextViewBrokerage,
            R.id.autoCompleteTextViewDeposit, R.id.autoCompleteTextViewPincode,
            R.id.autoCompleteTextViewLandmark, R.id.autoCompleteTextViewHouseNumber,
            R.id.autoCompleteTextViewArea, R.id.autoCompleteTextViewColony,
            R.id.autoCompleteTextViewCity
        )

        autoCompleteTextViews.forEach { autoCompleteTextViewId ->
            view?.findViewById<AutoCompleteTextView>(autoCompleteTextViewId)?.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start()
                } else {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
                }
            }
        }

        view?.findViewById<TextInputLayout>(R.id.textInputLayoutPropertyDescription)
            ?.editText?.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start()
                } else {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
                }
            }

        btnLiveLocation.setOnClickListener {
            getLocation()
        }
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            checkLocationSettingsAndRetrieveLocation()
        } else {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkLocationSettingsAndRetrieveLocation() {
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                retrieveLocation() // All location settings are satisfied, proceed to get location
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        exception.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(context, "Unable to resolve location settings", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(context, "Location settings are inadequate, and cannot be fixed here.", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun retrieveLocation() {
        Toast.makeText(context, "Getting location...", Toast.LENGTH_SHORT).show()
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    onLocationObtained(it)
                } ?: run {
                    Toast.makeText(context, "Unable to get location. Please try again.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error getting location: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun onLocationObtained(location: Location) {
        val longitude = location.longitude
        val latitude = location.latitude

        sharedPreferences.edit().apply {
            putFloat("longitude", longitude.toFloat())
            putFloat("latitude", latitude.toFloat())
            apply()
        }

        textViewLongitude.text = "Longitude: $longitude"
        textViewLatitude.text = "Latitude: $latitude"

        textViewLongitude.visibility = View.VISIBLE
        textViewLatitude.visibility = View.VISIBLE

        Toast.makeText(context, "Location obtained successfully", Toast.LENGTH_SHORT).show()
    }

    private fun loadSavedData() {
        val longitude = sharedPreferences.getFloat("longitude", 0f)
        val latitude = sharedPreferences.getFloat("latitude", 0f)

        if (longitude != 0f && latitude != 0f) {
            textViewLongitude.text = "Longitude: $longitude"
            textViewLatitude.text = "Latitude: $latitude"
            textViewLongitude.visibility = View.VISIBLE
            textViewLatitude.visibility = View.VISIBLE
        } else {
            textViewLongitude.visibility = View.GONE
            textViewLatitude.visibility = View.GONE
        }

        view?.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewRent)?.setText(sharedPreferences.getString("rent", ""))
        view?.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewParkingCharges)?.setText(sharedPreferences.getString("parkingCharges", ""))
        view?.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewBrokerage)?.setText(sharedPreferences.getString("brokerage", ""))
        view?.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextViewDeposit)?.setText(sharedPreferences.getString("deposit", ""))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }
}
