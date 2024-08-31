package com.avi.gharkhojo.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Model.Post
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.GridItemBinding
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class GridAdapter(
    private val listener: (Post) -> Unit
) : RecyclerView.Adapter<GridAdapter.ViewHolder>() {

    private var gridItemList: ArrayList<Post> = arrayListOf()
    inner class ViewHolder(private var gridItemBinding: GridItemBinding) : RecyclerView.ViewHolder(gridItemBinding.root) {
        fun bindItem(gridItem: Post) {
            // Load the main image
            Glide.with(gridItemBinding.image.context)
                .load(gridItem.coverImage)
                .into(gridItemBinding.image)

            // Load the display picture
            Glide.with(gridItemBinding.displayPicture.context)
                .load(R.drawable.kk)
                .into(gridItemBinding.displayPicture)

            // Set the rent text with the resource string
            gridItemBinding.rent.text = gridItemBinding.root.context.getString(R.string.rent_format, formatRent(gridItem.rent!!))
            gridItemBinding.location.text = "${gridItem.area} , ${gridItem.city}"

            // Set the BHK description text dynamically using resource string
            gridItemBinding.bhkDescription.text = gridItemBinding.root.context.getString(R.string.bhk_description
            ,"${gridItem.noOfBedRoom!!+gridItem.noOfKitchen!!+1}")
        }

        private fun formatRent(rent: String): String {
            val updatedRent = rent.replace(",", "").replace("₹", "").toDouble()
            val rentInThousands = updatedRent / 1000

            // Get currency instance for INR
            val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

            return if (rentInThousands >= 1) {
                currencyFormatter.maximumFractionDigits = 0
                "${currencyFormatter.format(rentInThousands)}k"
            } else {
                currencyFormatter.maximumFractionDigits = 2
                currencyFormatter.format(updatedRent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = GridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return gridItemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(gridItemList[position])
        holder.itemView.setOnClickListener {
            listener(gridItemList[position])
        }
    }
    fun updateData(newGridItemList: List<Post>) {
        gridItemList.clear()
        gridItemList.addAll(newGridItemList)
        notifyDataSetChanged()
    }
}
