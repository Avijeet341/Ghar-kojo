package com.avi.gharkhojo.Fragments.OwnerFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.avi.gharkhojo.R
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class PropertyDetailsFragment : Fragment() {
    private var bedroomCount = 0
    private var washroomCount = 0
    private var balconyCount = 0
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
        setupCounters(view)
        setupBackButton(view)
        hideNavigationBar()
    }
    private fun hideNavigationBar() {
        (activity as? AppCompatActivity)?.findViewById<ChipNavigationBar>(R.id.bottom_nav_bar_owner)?.visibility = View.GONE
    }
    private fun showNavigationBar() {
        (activity as? AppCompatActivity)?.findViewById<ChipNavigationBar>(R.id.bottom_nav_bar_owner)?.visibility = View.VISIBLE
    }
    override fun onDestroyView() {
        super.onDestroyView()
        // Show the navigation bar when leaving this fragment
        showNavigationBar()
    }
    private fun setupBackButton(view: View) {
        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            // Navigate back to the parent fragment
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupCounters(view: View) {
        val tvBedroomCount = view.findViewById<TextView>(R.id.tvBedroomCount)
        val tvWashroomCount = view.findViewById<TextView>(R.id.tvWashroomCount)
        val tvBalconyCount = view.findViewById<TextView>(R.id.tvBalconyCount)

        // Bedrooms
        view.findViewById<MaterialButton>(R.id.btnDecreaseBedrooms).setOnClickListener {
            if (bedroomCount > 0) bedroomCount--
            tvBedroomCount.text = bedroomCount.toString()
        }
        view.findViewById<MaterialButton>(R.id.btnIncreaseBedrooms).setOnClickListener {
            bedroomCount++
            tvBedroomCount.text = bedroomCount.toString()
        }

        // Washrooms
        view.findViewById<MaterialButton>(R.id.btnDecreaseWashrooms).setOnClickListener {
            if (washroomCount > 0) washroomCount--
            tvWashroomCount.text = washroomCount.toString()
        }
        view.findViewById<MaterialButton>(R.id.btnIncreaseWashrooms).setOnClickListener {
            washroomCount++
            tvWashroomCount.text = washroomCount.toString()
        }

        // Balconies
        view.findViewById<MaterialButton>(R.id.btnDecreaseBalconies).setOnClickListener {
            if (balconyCount > 0) balconyCount--
            tvBalconyCount.text = balconyCount.toString()
        }
        view.findViewById<MaterialButton>(R.id.btnIncreaseBalconies).setOnClickListener {
            balconyCount++
            tvBalconyCount.text = balconyCount.toString()
        }

        // Initialize TextView values
        tvBedroomCount.text = bedroomCount.toString()
        tvWashroomCount.text = washroomCount.toString()
        tvBalconyCount.text = balconyCount.toString()
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