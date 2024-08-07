package com.avi.gharkhojo.Fragments.OwnerFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import com.avi.gharkhojo.R
import com.google.android.material.textfield.TextInputLayout

class PropertyDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_property_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFurnishingSpinner(view)
    }

    private fun setupFurnishingSpinner(view: View) {
        val furnishingOptions = arrayOf("Semi Furnished", "Fully Furnished", "No Furnishing")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, furnishingOptions)

        val spinnerFurnishing = view.findViewById<TextInputLayout>(R.id.spinnerFurnishing)
            .findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)

        spinnerFurnishing.setAdapter(adapter)

        // Set the dropdown background to black
        spinnerFurnishing.setDropDownBackgroundResource(android.R.color.black)
    }
    companion object {
        @JvmStatic
        fun newInstance() = PropertyDetailsFragment()
    }
}