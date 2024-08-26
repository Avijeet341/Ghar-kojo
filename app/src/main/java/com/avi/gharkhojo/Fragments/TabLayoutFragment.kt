/*
* Agar tere ko naya tab item add karna hai to Model me ja kar FilterMode name ka enum class hoga usme add karna Category
* uske bad to tu janta hi hai
*
*
* */

package com.avi.gharkhojo.Fragments

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.avi.gharkhojo.Adapter.ImageAdapter
import com.avi.gharkhojo.Model.FilterMode
import com.avi.gharkhojo.Model.ImageItem
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentTabLayoutBinding
import com.google.android.material.chip.Chip

class TabLayoutFragment : Fragment() {

    private var _binding: FragmentTabLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageAdapter: ImageAdapter
    private var currentFilter = FilterMode.All

    private val filterCategories = listOf("All","Hall" ,"LivingRoom", "Office", "Bedroom", "Walk-in Robe")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTabLayoutBinding.inflate(inflater, container, false)
        imageAdapter = ImageAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupFilterChips()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupFilterChips() {
        binding.filterChipGroup.removeAllViews()
        binding.filterChipGroup.isSingleSelection = true
        binding.filterChipGroup.isSelectionRequired = false

        val chipStyle = R.style.CustomChipStyle
        val chipTextAppearance = R.style.CustomChipTextAppearance

        filterCategories.forEachIndexed { index, category ->
            val chip = Chip(requireContext(), null, chipStyle).apply {
                id = View.generateViewId()
                text = category
                isCheckable = true

                 //Custom Style
                setTextAppearanceResource(chipTextAppearance)
                setChipBackgroundColorResource(R.color.chip_background_color)
                minimumHeight = resources.getDimensionPixelSize(R.dimen.chip_min_height)
                shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                    .setAllCornerSizes(resources.getDimension(R.dimen.chip_corner_radius))
                    .build()
                chipStartPadding = resources.getDimension(R.dimen.chip_start_padding)
                chipEndPadding = resources.getDimension(R.dimen.chip_end_padding)
                setPadding(
                    resources.getDimensionPixelSize(R.dimen.chip_padding_start),
                    0,
                    resources.getDimensionPixelSize(R.dimen.chip_padding_end),
                    0
                )

                chipIconSize = 0f
                chipIcon = null
                checkedIcon = null

                setOnClickListener {
                    isChecked = true
                    currentFilter = FilterMode.entries.toTypedArray()[index]
                    updateImageList()
                    animateChipSelection(this)
                }
            }
            binding.filterChipGroup.addView(chip)
        }

        // by default  all checked rahega
        (binding.filterChipGroup.getChildAt(0) as? Chip)?.let {
            it.isChecked = true
            animateChipSelection(it)
        }
    }


    private fun animateChipSelection(selectedChip: Chip) {
        val icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_check)?.apply {
            setTint(ContextCompat.getColor(requireContext(), R.color.white))
        }

        selectedChip.chipIcon = icon
        selectedChip.isChipIconVisible = true

//Size shrink and expand
        selectedChip.chipIconSize = 1f

        val targetIconSize = selectedChip.height * 0.5f
        ValueAnimator.ofFloat(1f, targetIconSize).apply {
            addUpdateListener { animator ->
                val animatedValue = animator.animatedValue as Float
                selectedChip.chipIconSize = animatedValue
            }
            duration = 300
            interpolator = OvershootInterpolator()
            start()
        }

        binding.filterChipGroup.children.forEach { view ->
            if (view is Chip && view != selectedChip) {
                view.isChipIconVisible = false
            }
        }
    }

    private fun setupRecyclerView() {
        binding.imageGrid.apply {
            adapter = imageAdapter
            layoutManager = GridLayoutManager(context, 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (imageAdapter.getItemViewType(position) == ImageAdapter.VIEW_TYPE_HEADER) 2 else 1
                    }
                }
            }
        }
        updateImageList()
    }

    private fun updateImageList() {
        if (!::imageAdapter.isInitialized) return

        val filteredList = when (currentFilter) {
            FilterMode.All -> getAllImages()
            FilterMode.Hall -> listOf(ImageItem("Hall", HallImages))
            FilterMode.LivingRoom -> listOf(ImageItem("Living Room", LivingRoomImages))
            FilterMode.Office -> listOf(ImageItem("Office", OfficeImages))
            FilterMode.Bedroom -> listOf(ImageItem("Bedroom", BedroomImages))
            FilterMode.WalkInRobe -> listOf(ImageItem("Walk-in Robe", WalkInRobeImages))
        }
        val flattenedList = filteredList.flatMap { item ->
            listOf(ImageItem(item.title, emptyList())) + item.images.map { ImageItem("", listOf(it)) }
        }
        imageAdapter.submitList(flattenedList)
    }

    private fun getAllImages(): List<ImageItem> {
        return listOf(
            ImageItem("Hall", HallImages),
            ImageItem("Living Room", LivingRoomImages),
            ImageItem("Office", OfficeImages),
            ImageItem("Bedroom", BedroomImages),
            ImageItem("Walk-in Robe", WalkInRobeImages),
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        val WalkInRobeImages = listOf(R.drawable.home11)
        val LivingRoomImages = listOf(R.drawable.home6, R.drawable.home8, R.drawable.home9, R.drawable.home3)
        val BedroomImages = listOf(R.drawable.home2, R.drawable.home4, R.drawable.home7)
        val OfficeImages = listOf(R.drawable.home1, R.drawable.home5)
        val HallImages = listOf(R.drawable.home10,R.drawable.home12)
    }
}

