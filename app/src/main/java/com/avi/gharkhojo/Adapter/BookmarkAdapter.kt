package com.avi.gharkhojo.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Model.Post
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.UserBookmarkedItemBinding
import com.bumptech.glide.Glide

class BookmarkAdapter(
    private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {
    private  var post: ArrayList<Post> = ArrayList()
    class BookmarkViewHolder(val binding: UserBookmarkedItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = UserBookmarkedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val post = post[position]

        with(holder.binding) {
            Glide.with(root.context)
                .load(post.coverImage)
                .placeholder(R.drawable.family)
                .into(houseImage)

            houseTitle.text = post.propertyType
            houseLocation.text = "${post.city}, ${post.state}"
            housePriceAmount.text = "${post.rent}"
            houseBedrooms.text = post.noOfBedRoom.toString()
            houseBathrooms.text = post.noOfBathroom.toString()
            houseArea.text = post.builtUpArea

            root.setOnClickListener {
                onItemClick(post) // Trigger the click callback
            }
        }
    }

    fun updateData(newPost: List<Post>) {
        post.clear()
        post.addAll(newPost)
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int = post.size


}
