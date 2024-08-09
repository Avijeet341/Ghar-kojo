package com.avi.gharkhojo.Fragments.OwnerFragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.avi.gharkhojo.R
import java.util.concurrent.TimeUnit

class AddFragment : Fragment() {

    private lateinit var ownerNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var tenantServedEditText: EditText
    private lateinit var propertyTypeSpinner: Spinner
    private lateinit var preferredTenantsSpinner: Spinner
    private lateinit var phoneNumberEditText: EditText
    private lateinit var nextButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        initializeViews(view)
        setupSpinners()
        setupNextButton()
        loadData()
        return view
    }

    private fun initializeViews(view: View) {
        ownerNameEditText = view.findViewById(R.id.ownerNameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        tenantServedEditText = view.findViewById(R.id.tenantServedEditText)
        propertyTypeSpinner = view.findViewById(R.id.propertyTypeSpinner)
        preferredTenantsSpinner = view.findViewById(R.id.preferredTenantsSpinner)
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText)
        nextButton = view.findViewById(R.id.nextButton)
    }

    private fun setupSpinners() {
        val propertyTypes = listOf("House", "Apartment", "Flat", "Dormitory", "Luxury", "Commercial")
        val propertyTypeAdapter = AccessibleSpinnerAdapter(requireContext(), R.layout.custom_spinner_item, propertyTypes)
        propertyTypeAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        propertyTypeSpinner.adapter = propertyTypeAdapter

        val preferredTenants = listOf("Family", "Only Girls", "Only Boys", "Bachelors", "Any")
        val preferredTenantsAdapter = AccessibleSpinnerAdapter(requireContext(), R.layout.custom_spinner_item, preferredTenants)
        preferredTenantsAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item)
        preferredTenantsSpinner.adapter = preferredTenantsAdapter
    }

    private fun setupNextButton() {
        nextButton.setOnClickListener {
            if (validateInputs()) {
                saveData()
                findNavController().navigate(R.id.action_addFragment_to_propertyDetailsFragment)
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (ownerNameEditText.text.isNullOrBlank()) {
            ownerNameEditText.error = "Owner name is required"
            ownerNameEditText.announceForAccessibility("Error: Owner name is required")
            isValid = false
        }

        if (emailEditText.text.isNullOrBlank()) {
            emailEditText.error = "Email is required"
            emailEditText.announceForAccessibility("Error: Email is required")
            isValid = false
        }

        if (tenantServedEditText.text.isNullOrBlank()) {
            tenantServedEditText.error = "Tenant served is required"
            tenantServedEditText.announceForAccessibility("Error: Tenant served is required")
            isValid = false
        }

        if (phoneNumberEditText.text.isNullOrBlank()) {
            phoneNumberEditText.error = "Phone number is required"
            phoneNumberEditText.announceForAccessibility("Error: Phone number is required")
            isValid = false
        }

        return isValid
    }

    private fun saveData() {
        val sharedPref = activity?.getSharedPreferences("OwnerData", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("ownerName", ownerNameEditText.text.toString())
            putString("email", emailEditText.text.toString())
            putString("tenantServed", tenantServedEditText.text.toString())
            putString("propertyType", propertyTypeSpinner.selectedItem.toString())
            putString("preferredTenants", preferredTenantsSpinner.selectedItem.toString())
            putString("phoneNumber", phoneNumberEditText.text.toString())
            putLong("last_updated", System.currentTimeMillis())
            apply()
        }

        scheduleDataClear(requireContext())
    }

    private fun loadData() {
        val sharedPref = activity?.getSharedPreferences("OwnerData", Context.MODE_PRIVATE) ?: return
        val lastUpdated = sharedPref.getLong("last_updated", 0)
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastUpdated > 30000) {  //1 min in milliseconds
            clearData()
        } else {
            ownerNameEditText.setText(sharedPref.getString("ownerName", ""))
            emailEditText.setText(sharedPref.getString("email", ""))
            tenantServedEditText.setText(sharedPref.getString("tenantServed", ""))
            phoneNumberEditText.setText(sharedPref.getString("phoneNumber", ""))

            val propertyType = sharedPref.getString("propertyType", "")
            val propertyTypePosition = getSpinnerPosition(propertyTypeSpinner, propertyType)
            if (propertyTypePosition != -1) {
                propertyTypeSpinner.setSelection(propertyTypePosition)
            }

            val preferredTenants = sharedPref.getString("preferredTenants", "")
            val preferredTenantsPosition = getSpinnerPosition(preferredTenantsSpinner, preferredTenants)
            if (preferredTenantsPosition != -1) {
                preferredTenantsSpinner.setSelection(preferredTenantsPosition)
            }
        }
    }

    private fun getSpinnerPosition(spinner: Spinner, value: String?): Int {
        if (value.isNullOrEmpty()) return -1
        for (i in 0 until spinner.adapter.count) {
            if (value == spinner.adapter.getItem(i)) {
                return i
            }
        }
        return -1
    }

    private fun clearData() {
        val sharedPref = activity?.getSharedPreferences("OwnerData", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            remove("ownerName")
            remove("email")
            remove("tenantServed")
            remove("propertyType")
            remove("preferredTenants")
            remove("phoneNumber")
            remove("last_updated")
            apply()
        }
    }

    private fun scheduleDataClear(context: Context) {
        val sharedPref = context.getSharedPreferences("OwnerData", Context.MODE_PRIVATE) ?: return
        val hasData = sharedPref.contains("ownerName") || sharedPref.contains("email") ||
                sharedPref.contains("tenantServed") || sharedPref.contains("propertyType") ||
                sharedPref.contains("preferredTenants") || sharedPref.contains("phoneNumber")

        if (hasData) {
            val clearDataWorkRequest = OneTimeWorkRequestBuilder<ClearDataWorker>()
                .setInitialDelay(1, TimeUnit.MINUTES)  // Delay execution by 1 minute
                .build()
            WorkManager.getInstance(context).enqueue(clearDataWorkRequest)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddFragment()
    }
}

class AccessibleSpinnerAdapter(context: Context, resource: Int, objects: List<String>) :
    ArrayAdapter<String>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        if (view is TextView) {
            view.contentDescription = "Selected property type: ${getItem(position)}"
        }
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        view.setBackgroundResource(android.R.color.black)
        (view as? TextView)?.setTextColor(context.resources.getColor(android.R.color.white, null))
        return view
    }
}
