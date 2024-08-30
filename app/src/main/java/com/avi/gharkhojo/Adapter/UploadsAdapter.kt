package com.avi.gharkhojo.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Model.Post
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.OwnerUploadsItemBinding
import com.bumptech.glide.Glide

class UploadsAdapter(private val onItemClick: (Post) -> Unit) :
    RecyclerView.Adapter<UploadsAdapter.UploadViewHolder>() {
        private val uploads = ArrayList<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadViewHolder {
        val binding = OwnerUploadsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UploadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UploadViewHolder, position: Int) {
        holder.bind(uploads[position])
    }

    override fun getItemCount(): Int = uploads.size

    fun updateData(newUploads: MutableList<Post>) {
        this.uploads.clear()
        this.uploads.addAll(newUploads)
        notifyDataSetChanged()
    }

    inner class UploadViewHolder(private val binding: OwnerUploadsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            Glide.with(binding.root.context)
                .load(post.coverImage.orEmpty())
                .error(R.drawable.home)
                .into(binding.houseImage)

            binding.houseTitle.text = post.propertyType.orEmpty()
            binding.houseLocation.text = "${post.area.orEmpty()}, ${post.city.orEmpty()}"
            binding.housePriceAmount.text = "${post.rent}"
            binding.housePricePeriod.text = "/Month"
            binding.houseBedrooms.text = post.noOfBedRoom.toString()
            binding.houseBathrooms.text = post.noOfBathroom.toString()
            binding.houseArea.text ="${post.builtUpArea} sq.ft."

            binding.root.setOnClickListener {
                onItemClick(post)
            }
        }
    }
}
