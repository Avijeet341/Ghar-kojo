package com.avi.gharkhojo.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Model.Upload
import com.avi.gharkhojo.databinding.OwnerUploadsItemBinding
import com.bumptech.glide.Glide

class UploadsAdapter(private val uploads: List<Upload>, private val onItemClick: (Upload) -> Unit) :
    RecyclerView.Adapter<UploadsAdapter.UploadViewHolder>() {

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

    inner class UploadViewHolder(private val binding: OwnerUploadsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(upload: Upload) {
            Glide.with(binding.root.context)
                .load(upload.imageResId)
                .into(binding.houseImage)

            binding.houseTitle.text = upload.title
            binding.houseLocation.text = upload.location
            binding.housePriceAmount.text = upload.priceAmount
            binding.housePricePeriod.text = upload.pricePeriod
            binding.houseBedrooms.text = upload.bedrooms.toString()
            binding.houseBathrooms.text = upload.bathrooms.toString()
            binding.houseArea.text = upload.area

            binding.root.setOnClickListener {
                onItemClick(upload)
            }
        }
    }
}
