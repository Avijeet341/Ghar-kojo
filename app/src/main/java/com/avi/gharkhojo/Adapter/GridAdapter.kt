package com.avi.gharkhojo.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Model.Post
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.GridItemBinding
import com.avi.gharkhojo.databinding.ShimmerGridItemBinding
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class GridAdapter(
    private val listener: (Post) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SHIMMER = 0
    private val VIEW_TYPE_NORMAL = 1

    private var gridItemList: ArrayList<Post> = arrayListOf()
    var isLoading: Boolean = true
    // The number of skeleton items to show
    private val skeletonItemCount = 6

    inner class ViewHolder(private var gridItemBinding: GridItemBinding) :
        RecyclerView.ViewHolder(gridItemBinding.root) {
        fun bindItem(gridItem: Post) {
            Glide.with(gridItemBinding.image.context)
                .load(gridItem.coverImage)
                .into(gridItemBinding.image)

            // Load the display picture
            Glide.with(gridItemBinding.displayPicture.context)
                .load(R.drawable.kk)
                .into(gridItemBinding.displayPicture)

            // Set the rent text with the resource string
            gridItemBinding.rent.text =
                gridItemBinding.root.context.getString(
                    R.string.rent_format,
                    formatRent(gridItem.rent!!)
                )
            gridItemBinding.location.text = "${gridItem.area} , ${gridItem.city}"

            // Set the BHK description text dynamically using resource string
            gridItemBinding.bhkDescription.text =
                gridItemBinding.root.context.getString(
                    R.string.bhk_description,
                    "${gridItem.noOfBedRoom!! + gridItem.noOfKitchen!! + 1}"
                )
        }

        private fun formatRent(rent: String): String {
            val updatedRent = rent.replace(",", "").replace("₹", "").toDouble()
            val rentInThousands = updatedRent / 1000

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

    inner class ShimmerViewHolder(private val shimmerBinding: ShimmerGridItemBinding) :
        RecyclerView.ViewHolder(shimmerBinding.root)

    override fun getItemViewType(position: Int): Int {
        return if (isLoading) VIEW_TYPE_SHIMMER else VIEW_TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_NORMAL) {
            val binding =
                GridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolder(binding)
        } else {
            val shimmerBinding = ShimmerGridItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ShimmerViewHolder(shimmerBinding)
        }
    }

    override fun getItemCount(): Int {
        return if (isLoading) skeletonItemCount else gridItemList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == VIEW_TYPE_NORMAL) {
            val viewHolder = holder as ViewHolder
            val post = gridItemList[position]
            viewHolder.bindItem(post)
            holder.itemView.setOnClickListener {
                listener(post)
            }
        } // For shimmer view type, no binding is needed because shimmer auto-runs.
    }

    fun updateData(newGridItemList: List<Post>) {
        isLoading = false
        gridItemList.clear()
        gridItemList.addAll(newGridItemList)
        notifyDataSetChanged()
    }
}
