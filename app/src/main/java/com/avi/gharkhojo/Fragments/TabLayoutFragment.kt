package com.avi.gharkhojo.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentTabLayoutBinding
import com.bumptech.glide.Glide

class TabLayoutFragment : Fragment() {

    private var _binding: FragmentTabLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageAdapter: ImageAdapter
    private var currentFilter = FilterMode.All

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTabLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupFilterChips()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupFilterChips() {
        binding.filterChipGroup.setOnCheckedChangeListener { _, checkedId ->
            currentFilter = when (checkedId) {
                R.id.filter_all -> FilterMode.All
                R.id.filter_living_room -> FilterMode.LivingRoom
                R.id.filter_office -> FilterMode.Office
                R.id.filter_bedroom -> FilterMode.Bedroom
                R.id.filter_walk_in_robe -> FilterMode.WalkInRobe
                else -> FilterMode.All
            }
            updateImageList()
        }
    }

    private fun setupRecyclerView() {
        imageAdapter = ImageAdapter()
        binding.imageGrid.adapter = imageAdapter
        binding.imageGrid.layoutManager = GridLayoutManager(context, 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (imageAdapter.getItemViewType(position) == ImageAdapter.VIEW_TYPE_HEADER) 2 else 1
                }
            }
        }
        updateImageList()
    }

    private fun updateImageList() {
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
        _binding = null
    }

    companion object {
        val WalkInRobeImages = listOf(R.drawable.home11)
        val LivingRoomImages = listOf(R.drawable.home6, R.drawable.home8, R.drawable.home9, R.drawable.home3)
        val BedroomImages = listOf(R.drawable.home2, R.drawable.home4, R.drawable.home7)
        val OfficeImages = listOf(R.drawable.home1, R.drawable.home5)
    }
}

class ImageAdapter : ListAdapter<ImageItem, RecyclerView.ViewHolder>(ImageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false))
            VIEW_TYPE_IMAGE -> ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is HeaderViewHolder -> holder.bind(item.title)
            is ImageViewHolder -> item.images.firstOrNull()?.let { holder.bind(it) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).images.isEmpty()) VIEW_TYPE_HEADER else VIEW_TYPE_IMAGE
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleText: TextView = view.findViewById(R.id.title_text)

        fun bind(title: String) {
            titleText.text = title
        }
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.image_view)

        fun bind(imageRes: Int) {
            Glide.with(imageView.context)
                .load(imageRes)
                .override(500, 500)
                .centerCrop()
                .into(imageView)
        }
    }

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_IMAGE = 1
    }
}

class ImageDiffCallback : DiffUtil.ItemCallback<ImageItem>() {
    override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem.title == newItem.title && oldItem.images == newItem.images
    }

    override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem): Boolean {
        return oldItem == newItem
    }
}

data class ImageItem(val title: String, val images: List<Int>)

enum class FilterMode {
    All,
    LivingRoom,
    Office,
    Bedroom,
    WalkInRobe
}