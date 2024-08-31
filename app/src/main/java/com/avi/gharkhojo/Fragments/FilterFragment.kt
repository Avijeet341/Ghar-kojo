package com.avi.gharkhojo.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.avi.gharkhojo.CustomSlider.HistogramRangeSlider
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentFilterBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.text.NumberFormat
import java.util.Locale

class FilterFragment : Fragment(), HistogramRangeSlider.OnRangeChangeListener {

    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!

    private val maxAllowedPrice = 50000f
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        val view = binding.root

        setupHistogramRangeSlider()
        setupHistogramRangeSliderArea()
        setupButtons()
        setupAreaChipGroup()

        return view
    }

    private fun setupHistogramRangeSlider() {
        binding.histogramRangeSlider.onRangeChangeListener = this

        // Sample data for budget slider
        val sampleData = listOf(
            5f, 8f, 12f, 18f, 25f, 35f, 48f, 64f, 85f, 110f,
            140f, 175f, 215f, 260f, 310f, 365f, 425f, 490f, 560f, 635f,
            715f, 800f, 890f, 985f, 1000f, 985f, 890f, 800f, 715f, 635f,
            560f, 490f, 425f, 365f, 310f, 260f, 215f, 175f, 140f, 110f,
            85f, 64f, 48f, 35f, 25f, 18f, 12f, 8f, 5f, 3f,
            // Extended tail
            2f, 2f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f
        )
        binding.histogramRangeSlider.setHistogramData(sampleData)
        binding.histogramRangeSlider.setValueRange(1500f, 50000f)
    }

    private fun setupHistogramRangeSliderArea() {
        binding.histogramRangeSliderArea.onRangeChangeListener = object : HistogramRangeSlider.OnRangeChangeListener {
            override fun onRangeChanged(minValue: Float, maxValue: Float) {
                val formattedMinArea = String.format("%,d", minValue.toInt())
                val formattedMaxArea = String.format("%,d", maxValue.toInt())

                binding.minAreaTextView.text = formattedMinArea
                binding.maxAreaTextView.text = formattedMaxArea

                // Update maxAreaUnitTextView based on maxValue
                if (maxValue >= 3000) {
                    binding.maxAreaUnitTextView.text = "Sq.ft+"
                } else {
                    binding.maxAreaUnitTextView.text = "Sq.ft"
                }
            }
        }

        // Sample data for built-up area slider
        val areaData = listOf(
            100f, 150f, 200f, 250f, 300f, 350f, 400f, 450f, 500f, 600f,
            700f, 800f, 900f, 1000f, 1200f, 1400f, 1600f, 1800f, 2000f, 2200f,
            2400f, 2600f, 2800f, 3000f, 2800f, 2600f, 2400f, 2200f, 2000f, 1800f,
            1600f, 1400f, 1200f, 1000f, 900f, 800f, 700f, 600f, 500f, 400f
        )
        binding.histogramRangeSliderArea.setHistogramData(areaData)
        binding.histogramRangeSliderArea.setValueRange(800f, 3000f)
    }

    private fun setupButtons() {
        binding.clearFilterButton.setOnClickListener {
            clearAllFilters()
        }

        binding.searchButton.setOnClickListener {
            applyFilters()
        }
    }

    private fun setupAreaChipGroup() {
        binding.addAreaButton.setOnClickListener {
            val areaName = binding.areaInput.text.toString().trim()
            if (areaName.isNotEmpty()) {
                addAreaChip(areaName)
                binding.areaInput.text.clear()
            }
        }
    }

    private fun addAreaChip(areaName: String) {
        val chip = Chip(context)
        chip.text = areaName
        chip.isCloseIconVisible = true
        chip.setChipBackgroundColorResource(R.color.expBlue)
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        chip.setCloseIconTintResource(R.color.white)

        chip.setOnCloseIconClickListener {
            binding.areaChipGroup.removeView(chip)
        }

        binding.areaChipGroup.addView(chip)
    }

    private fun clearAllFilters() {
        binding.propertyTypeChipGroup.clearCheck()
        binding.tenantTypeChipGroup.clearCheck()
        binding.bedroomsChipGroup.clearCheck()
        binding.washroomsChipGroup.clearCheck()
        binding.balconyChipGroup.clearCheck()
        binding.floorChipGroup.clearCheck()
        binding.areaChipGroup.removeAllViews()

        binding.histogramRangeSlider.resetSlider()
        binding.histogramRangeSliderArea.resetSlider()

        binding.liftService.isChecked = false
        binding.generatorService.isChecked = false
        binding.gasService.isChecked = false
        binding.securityService.isChecked = false
        binding.parkingService.isChecked = false

        binding.sortByRadioGroup.clearCheck()
    }

    private fun applyFilters() {
        val sortOption = when (binding.sortByRadioGroup.checkedRadioButtonId) {
            R.id.sort_low_to_high -> "Cost: Low to High"
            R.id.sort_high_to_low -> "Cost: High to Low"
            else -> null
        }
        val propertyType = getSelectedChipText(binding.propertyTypeChipGroup)
        val tenantType = getSelectedChipText(binding.tenantTypeChipGroup)
        val bedrooms = getSelectedChipText(binding.bedroomsChipGroup)
        val washrooms = getSelectedChipText(binding.washroomsChipGroup)
        val balcony = getSelectedChipText(binding.balconyChipGroup)
        val floor = getSelectedChipText(binding.floorChipGroup)
        val areas = getAreaChips()

        val budgetRange = binding.histogramRangeSlider.getSelectedRange()
        val areaRange = binding.histogramRangeSliderArea.getSelectedRange()

        val services = mutableListOf<String>()
        if (binding.liftService.isChecked) services.add("Lift")
        if (binding.generatorService.isChecked) services.add("Generator")
        if (binding.gasService.isChecked) services.add("Gas")
        if (binding.securityService.isChecked) services.add("Security Guard")
        if (binding.parkingService.isChecked) services.add("Parking")

        println("Filters applied:")
        println("Sort: $sortOption")
        println("Property Type: $propertyType")
        println("Tenant Type: $tenantType")
        println("Bedrooms: $bedrooms")
        println("Washrooms: $washrooms")
        println("Balcony: $balcony")
        println("Floor: $floor")
        println("Areas: $areas")
        println("Budget Range: ₹${budgetRange.first.toInt()} - ₹${budgetRange.second.toInt()}")
        println("Area Range: ${areaRange.first.toInt()} Sq.ft - ${areaRange.second.toInt()} Sq.ft")
        println("Services: $services")
    }

    private fun getSelectedChipText(chipGroup: ChipGroup): String? {
        val selectedChipId = chipGroup.checkedChipId
        return if (selectedChipId != -1) {
            view?.findViewById<Chip>(selectedChipId)?.text.toString()
        } else {
            null
        }
    }

    private fun getAreaChips(): List<String> {
        return (0 until binding.areaChipGroup.childCount)
            .map { binding.areaChipGroup.getChildAt(it) as Chip }
            .map { it.text.toString() }
    }

    override fun onRangeChanged(minPrice: Float, maxPrice: Float) {
        currencyFormat.maximumFractionDigits = 0
        val formattedMinPrice = currencyFormat.format(minPrice.toInt())
        val formattedMaxPrice = if (maxPrice >= maxAllowedPrice) {
            "${currencyFormat.format(maxPrice.toInt())}+"
        } else {
            currencyFormat.format(maxPrice.toInt())
        }

        binding.minPriceTextView.text = formattedMinPrice
        binding.maxPriceTextView.text = formattedMaxPrice
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
