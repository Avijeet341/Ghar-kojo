package com.avi.gharkhojo.Adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.avi.gharkhojo.R

class PhotoAdapter(private var photos: List<Uri>) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.photoImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoUri = photos[position]
        Glide.with(holder.imageView.context)
            .load(photoUri)
            .centerCrop()
            .placeholder(R.drawable.india)
            .error(R.drawable.india)
            .into(holder.imageView)
    }

    override fun getItemCount() = photos.size

    fun updatePhotos(newPhotos: List<Uri>) {
        photos = newPhotos
        notifyDataSetChanged()
    }
}