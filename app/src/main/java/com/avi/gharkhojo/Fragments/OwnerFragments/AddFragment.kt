package com.avi.gharkhojo.Fragments.OwnerFragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.avi.gharkhojo.Model.PostDetails
import com.avi.gharkhojo.R
import com.ismaeldivita.chipnavigation.ChipNavigationBar
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
                PostDetails.let {
                    it.ownerName = ownerNameEditText.text.toString()
                    it.email = emailEditText.text.toString()
                    it.tenantServed = tenantServedEditText.text.toString()
                    it.propertyType = propertyTypeSpinner.selectedItem.toString()
                    it.preferredTenants = preferredTenantsSpinner.selectedItem.toString()
                    it.phoneNumber = phoneNumberEditText.text.toString()
                }
                hideNavigationBar()
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
    private fun hideNavigationBar() {
        (activity as? AppCompatActivity)?.findViewById<ChipNavigationBar>(R.id.bottom_nav_bar_owner)?.visibility = View.GONE
    }
    private fun showNavigationBar() {
        (activity as? AppCompatActivity)?.findViewById<ChipNavigationBar>(R.id.bottom_nav_bar_owner)?.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        showNavigationBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        PostDetails.clearAll()
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