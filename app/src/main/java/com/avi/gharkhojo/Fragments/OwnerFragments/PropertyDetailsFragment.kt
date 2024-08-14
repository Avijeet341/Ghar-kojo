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
import androidx.navigation.fragment.findNavController
import com.avi.gharkhojo.databinding.FragmentPropertyDetailsBinding

class PropertyDetailsFragment : Fragment() {
    private var bedroomCount = 0
    private var washroomCount = 0
    private var balconyCount = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_property_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFurnishingSpinner(view)
        setupCounters(view)
        setupBackButton(view)
        setupNextButton(view)
    }


    private fun setupBackButton(view: View) {
        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    private fun setupNextButton(view: View) {
        view.findViewById<MaterialButton>(R.id.btnNext).setOnClickListener {
            findNavController().navigate(R.id.action_propertyDetailsFragment_to_rentAndLocationFragment)
        }
    }

    private fun setupCounters(view: View) {
        val tvBedroomCount = view.findViewById<TextView>(R.id.tvBedroomCount)
        val tvWashroomCount = view.findViewById<TextView>(R.id.tvWashroomCount)
        val tvBalconyCount = view.findViewById<TextView>(R.id.tvBalconyCount)


        view.findViewById<MaterialButton>(R.id.btnDecreaseBedrooms).setOnClickListener {
            if (bedroomCount > 0) bedroomCount--
            tvBedroomCount.text = bedroomCount.toString()
        }
        view.findViewById<MaterialButton>(R.id.btnIncreaseBedrooms).setOnClickListener {
            bedroomCount++
            tvBedroomCount.text = bedroomCount.toString()
        }


        view.findViewById<MaterialButton>(R.id.btnDecreaseWashrooms).setOnClickListener {
            if (washroomCount > 0) washroomCount--
            tvWashroomCount.text = washroomCount.toString()
        }
        view.findViewById<MaterialButton>(R.id.btnIncreaseWashrooms).setOnClickListener {
            washroomCount++
            tvWashroomCount.text = washroomCount.toString()
        }


        view.findViewById<MaterialButton>(R.id.btnDecreaseBalconies).setOnClickListener {
            if (balconyCount > 0) balconyCount--
            tvBalconyCount.text = balconyCount.toString()
        }
        view.findViewById<MaterialButton>(R.id.btnIncreaseBalconies).setOnClickListener {
            balconyCount++
            tvBalconyCount.text = balconyCount.toString()
        }

        tvBedroomCount.text = bedroomCount.toString()
        tvWashroomCount.text = washroomCount.toString()
        tvBalconyCount.text = balconyCount.toString()
    }
    private fun isAllFieldsFilled(): Boolean {
        TODO("Not yet implemented")
    }
    private fun setupFurnishingSpinner(view: View) {
        val furnishingOptions = arrayOf("Semi Furnished", "Fully Furnished", "No Furnishing")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, furnishingOptions)

        val spinnerFurnishing = view.findViewById<TextInputLayout>(R.id.spinnerFurnishing)
            .findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)

        spinnerFurnishing.setAdapter(adapter)

        spinnerFurnishing.setDropDownBackgroundResource(android.R.color.black)
    }

}