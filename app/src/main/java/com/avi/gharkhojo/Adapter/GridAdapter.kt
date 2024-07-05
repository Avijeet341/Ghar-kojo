package com.avi.gharkhojo.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Model.GridItem
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.GridItemBinding
import com.bumptech.glide.Glide

class GridAdapter(
    private val gridItemList: ArrayList<GridItem>,
    private val listener: (GridItem, Int) -> Unit
) : RecyclerView.Adapter<GridAdapter.ViewHolder>() {

    inner class ViewHolder(private var gridItemBinding: GridItemBinding) : RecyclerView.ViewHolder(gridItemBinding.root) {
        fun bindItem(gridItem: GridItem) {
            // Load the main image
            Glide.with(gridItemBinding.image.context)
                .load(gridItem.imageResId)
                .into(gridItemBinding.image)

            // Load the display picture
            Glide.with(gridItemBinding.displayPicture.context)
                .load(R.drawable.kk)
                .into(gridItemBinding.displayPicture)

            // Set the rent text with the fixed prefix "Rent: "
            gridItemBinding.rent.text = "Rent: ${formatRent(gridItem.rent)}"

            // Set the BHK description text dynamically
            gridItemBinding.bhkDescription.text = "${gridItem.bhk}BHK"
        }

        private fun formatRent(rent: String): String {
            return if (rent.endsWith("000")) {
                "${rent.dropLast(3)}k"
            } else {
                rent
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
            listener(gridItemList[position], position)
        }
    }
}
