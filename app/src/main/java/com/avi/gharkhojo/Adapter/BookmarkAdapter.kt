package com.avi.gharkhojo.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Model.House
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.UserBookmarkedItemBinding
import com.bumptech.glide.Glide

class BookmarkAdapter(
    private val houses: List<House>,
    private val onItemClick: (House) -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    class BookmarkViewHolder(val binding: UserBookmarkedItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = UserBookmarkedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val house = houses[position]

        with(holder.binding) {
            Glide.with(root.context)
                .load(house.imageUrl)
                .placeholder(R.drawable.family)
                .into(houseImage)

            houseTitle.text = house.title
            houseLocation.text = house.location
            housePriceAmount.text = house.priceAmount
            houseBedrooms.text = house.bedrooms
            houseBathrooms.text = house.bathrooms
            houseArea.text = house.area

            root.setOnClickListener {
                onItemClick(house) // Trigger the click callback
            }
        }
    }

    override fun getItemCount(): Int = houses.size
}
