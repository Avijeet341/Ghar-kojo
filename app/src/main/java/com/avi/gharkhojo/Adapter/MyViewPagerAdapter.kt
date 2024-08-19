package com.avi.gharkhojo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.R
import com.bumptech.glide.Glide

class MyViewPagerAdapter(
    private val imageResIds: List<Int>
) : RecyclerView.Adapter<MyViewPagerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.photoImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageResId = imageResIds[position]
        Glide.with(holder.itemView.context)
            .load(imageResId)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageResIds.size
    }
}