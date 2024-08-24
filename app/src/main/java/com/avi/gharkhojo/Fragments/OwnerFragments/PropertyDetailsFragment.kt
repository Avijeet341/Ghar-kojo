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
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import androidx.navigation.fragment.findNavController
import com.avi.gharkhojo.Model.PostDetails
import com.avi.gharkhojo.databinding.FragmentPropertyDetailsBinding
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText

class PropertyDetailsFragment : Fragment() {
    private var bedroomCount = 0
    private var washroomCount = 0
    private var balconyCount = 0
    lateinit var tvBedroomCount:TextView
    lateinit var tvWashroomCount:TextView
    lateinit var tvBalconyCount:TextView
    lateinit var spinnerFurnishing:AutoCompleteTextView
    lateinit var etTotalSpace:TextInputEditText
    lateinit var etFloorPosition:TextInputEditText
    lateinit var etLockInPeriod:TextInputEditText
    lateinit var cbDiningSpace:MaterialCheckBox
    lateinit var switchLiftService:SwitchMaterial
    lateinit var switchGeneratorService:SwitchMaterial
    lateinit var switchGasService:SwitchMaterial
    lateinit var switchSecurityGaurdService:SwitchMaterial
    lateinit var switchParking:SwitchMaterial

    private var fragmentPropertyDetailsBinding: FragmentPropertyDetailsBinding? = null
    private val binding get() = fragmentPropertyDetailsBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPropertyDetailsBinding = FragmentPropertyDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialisation()
        setupFurnishingSpinner()
        setupCounters()

        binding.btnDelete.setOnClickListener {
            clearAll()
        }
    }

    private fun initialisation() {
         tvBedroomCount = binding.tvBedroomCount
         tvWashroomCount = binding.tvWashroomCount
         tvBalconyCount = binding.tvBalconyCount

        etTotalSpace = binding.etTotalSpace
        etFloorPosition = binding.etFloorPosition
        etLockInPeriod = binding.etLockInPeriod
        cbDiningSpace = binding.cbDiningSpace
        switchLiftService = binding.switchLiftService
        switchGeneratorService = binding.switchGeneratorService
        switchGasService = binding.switchGasService
        switchSecurityGaurdService = binding.switchSecurityGaurdService
        switchParking = binding.switchParking
        spinnerFurnishing = binding.autoCompleteTextView



        binding.btnDecreaseBedrooms.setOnClickListener {
            if (bedroomCount > 0) {
                bedroomCount--
            }
            tvBedroomCount.text = bedroomCount.toString()
        }
        binding.btnIncreaseBedrooms.setOnClickListener {
            bedroomCount++
            tvBedroomCount.error = null
            tvBedroomCount.text = bedroomCount.toString()
        }


        binding.btnDecreaseWashrooms.setOnClickListener {
            if (washroomCount > 0) {
                washroomCount--

            }
            tvWashroomCount.text = washroomCount.toString()
        }
        binding.btnIncreaseWashrooms.setOnClickListener {
            washroomCount++
            tvWashroomCount.error = null
            tvWashroomCount.text = washroomCount.toString()
        }


        binding.btnDecreaseBalconies.setOnClickListener {
            if (balconyCount > 0) {
                balconyCount--

            }
            tvBalconyCount.text = balconyCount.toString()
        }
        binding.btnIncreaseBalconies.setOnClickListener {
            balconyCount++
            tvBalconyCount.error = null
            tvBalconyCount.text = balconyCount.toString()
        }

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnNext.setOnClickListener {
            if(!isAllFieldsFilled()){
                return@setOnClickListener
            }
            with(PostDetails){
                builtUpArea = etTotalSpace.text.toString()
                floorPosition = etFloorPosition.text.toString()
                lockInPeriod = etLockInPeriod.text.toString().toInt()
                isAvailableDiningSpace = cbDiningSpace.isChecked
                hasLift = switchLiftService.isChecked
                hasGenerator = switchGeneratorService.isChecked
                hasGasService = switchGasService.isChecked
                hasSecurityGuard = switchSecurityGaurdService.isChecked
                hasParking = switchParking.isChecked
                furnished = spinnerFurnishing.text.toString()
                noOfBedRoom = this@PropertyDetailsFragment.bedroomCount
                noOfBathroom = this@PropertyDetailsFragment.washroomCount
                noOfBalcony = this@PropertyDetailsFragment.balconyCount

            }
            findNavController().navigate(R.id.action_propertyDetailsFragment_to_rentAndLocationFragment)
        }

    }

    private fun setupCounters() {

        tvBedroomCount.text = bedroomCount.toString()
        tvWashroomCount.text = washroomCount.toString()
        tvBalconyCount.text = balconyCount.toString()
    }
    private fun isAllFieldsFilled(): Boolean {
       if(etTotalSpace.text?.trim().isNullOrEmpty()){
           etTotalSpace.error = "This field is required"
           return false
       }
       if(etFloorPosition.text?.trim().isNullOrEmpty()){
           etFloorPosition.error = "This field is required"
           return false
       }
        if(etLockInPeriod.text?.trim().isNullOrEmpty()){
            etLockInPeriod.error = "This field is required"
            return false
        }
        if(spinnerFurnishing.text?.trim().isNullOrEmpty()){
            spinnerFurnishing.error = "This field is required"
            return false
        }
        if(bedroomCount == 0){
            tvBedroomCount.error = "This field is required"
            return false
        }
        if(washroomCount == 0){
            tvWashroomCount.error = "This field is required"
            return false
        }
        return true
    }
    private fun setupFurnishingSpinner() {
        val furnishingOptions = arrayOf("Semi Furnished", "Fully Furnished", "No Furnishing")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, furnishingOptions)

        spinnerFurnishing.setAdapter(adapter)

        spinnerFurnishing.setDropDownBackgroundResource(android.R.color.black)
        spinnerFurnishing.setOnItemClickListener { _, _, position, _ ->
            spinnerFurnishing.error = null
        }
    }

    fun clearAll(){
        etTotalSpace.text?.clear()
        etFloorPosition.text?.clear()
        etLockInPeriod.text?.clear()
        cbDiningSpace.isChecked = false
        switchLiftService.isChecked = false
        switchGeneratorService.isChecked = false
        switchGasService.isChecked = false
        switchSecurityGaurdService.isChecked = false
        switchParking.isChecked = false
        spinnerFurnishing.text?.clear()
        bedroomCount = 0
        washroomCount = 0
        balconyCount = 0
        tvBedroomCount.text = bedroomCount.toString()
        tvWashroomCount.text = washroomCount.toString()
        tvBalconyCount.text = balconyCount.toString()

    }

}