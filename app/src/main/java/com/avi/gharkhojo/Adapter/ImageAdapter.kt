package com.avi.gharkhojo.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Model.ImageItem
import com.avi.gharkhojo.R
import com.bumptech.glide.Glide

class ImageAdapter : ListAdapter<ImageItem, RecyclerView.ViewHolder>(ImageDiffCallback()) {

    companion object {
        const val VIEW_TYPE_HEADER = 0
        const val VIEW_TYPE_IMAGE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false)
            )
            VIEW_TYPE_IMAGE -> ImageViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(getItem(position).title)
            is ImageViewHolder -> holder.bind(getItem(position).images.first())
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).images.isEmpty()) VIEW_TYPE_HEADER else VIEW_TYPE_IMAGE
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.headerTitle)

        fun bind(title: String) {
            titleTextView.text = title
        }
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image)

        fun bind(imageRes: Int) {
            Glide.with(itemView.context).load(imageRes).into(imageView)
        }
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
