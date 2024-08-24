package com.avi.gharkhojo.Fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.avi.gharkhojo.Adapter.ImageAdapter
import com.avi.gharkhojo.MainActivity
import com.avi.gharkhojo.Model.ImageItem
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentTabLayoutBinding
import com.google.android.material.chip.Chip

class TabLayoutFragment : Fragment() {

    private var _binding: FragmentTabLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageAdapter: ImageAdapter
    private var currentFilter = FilterMode.All

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
       // hideBottomNavBar()
        setupToolbar()
        setupFilterChips()
        setupRecyclerView()
    }

    private fun hideBottomNavBar() {
        (activity as? MainActivity)?.hideBottomNavBar()
    }
    private fun showBottomNavBar() {
        (activity as? MainActivity)?.showBottomNavBar()
    }


    private fun setupToolbar() {
        (activity as AppCompatActivity).apply {
            setSupportActionBar(binding.toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupFilterChips() {
        val chips = listOf(
            binding.filterAll,
            binding.filterLivingRoom,
            binding.filterOffice,
            binding.filterBedroom,
            binding.filterWalkInRobe
        )

        chips.forEachIndexed { index, chip ->
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    currentFilter = FilterMode.values()[index]
                    updateImageList()
                    animateChipSelection(chip)
                }
            }
        }


        binding.filterAll.isChecked = true
    }

    private fun animateChipSelection(selectedChip: Chip) {
        val icon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_check)?.apply {
            setTint(ContextCompat.getColor(requireContext(), R.color.white))
        }

        selectedChip.chipIcon = icon
        selectedChip.isChipIconVisible = true


        selectedChip.chipIconSize = 0f


        val targetIconSize = selectedChip.height * 0.5f
        val iconSizeAnimator = ObjectAnimator.ofFloat(selectedChip, "chipIconSize", 0f, targetIconSize)

        AnimatorSet().apply {
            playTogether(iconSizeAnimator)
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
            ImageItem("Office", OfficeImages),
            ImageItem("Walk-in Robe", WalkInRobeImages),
            ImageItem("Living Room", LivingRoomImages),
            ImageItem("Bedroom", BedroomImages)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //showBottomNavBar()
        _binding = null
    }

    companion object {
        val WalkInRobeImages = listOf(R.drawable.home11)
        val LivingRoomImages = listOf(R.drawable.home6, R.drawable.home8, R.drawable.home9, R.drawable.home3)
        val BedroomImages = listOf(R.drawable.home2, R.drawable.home4, R.drawable.home7)
        val OfficeImages = listOf(R.drawable.home1, R.drawable.home5)
    }
}

enum class FilterMode {
    All,
    LivingRoom,
    Office,
    Bedroom,
    WalkInRobe
}
