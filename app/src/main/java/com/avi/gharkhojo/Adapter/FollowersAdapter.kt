package com.avi.gharkhojo.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.ItemInterestedUserBinding
import com.bumptech.glide.Glide
import io.grpc.Context

class FollowersAdapter(val onMessageClick:(String)->Unit):
   ListAdapter<Map<String, String>,FollowersAdapter.followerViewHolder>(Update()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): followerViewHolder {
        return followerViewHolder(ItemInterestedUserBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(
        holder: followerViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
        holder.binding.messageButton.setOnClickListener {
            onMessageClick(getItem(position)["userId"]!!)
        }
    }

    class followerViewHolder(val binding: ItemInterestedUserBinding):
        RecyclerView.ViewHolder(binding.root){

            fun bind(follower: Map<String, String>){
                binding.userName.text = follower["username"]
                Glide.with(this@followerViewHolder.itemView.context)
                    .load(follower["userImage"])
                    .centerCrop()
                    .circleCrop()
                    .error(R.drawable.baseline_person_24)
                    .into(binding.userImage)

                binding.interestedDate.text = ""
            }

        }

    fun updateList(newList: List<Map<String,String>>){
        submitList(newList)
    }

    class Update: DiffUtil.ItemCallback<Map<String,String>>(){
        override fun areItemsTheSame(
            oldItem: Map<String, String>,
            newItem: Map<String, String>
        ): Boolean {
            return oldItem.isSame(newItem)
        }

        override fun areContentsTheSame(
            oldItem: Map<String, String>,
            newItem: Map<String, String>
        ): Boolean {
           return oldItem.isSame(newItem)
        }

    }
}



private fun Map<String,String>.isSame(other: Map<String,String>): Boolean {
    return this["id"] == other["id"]
}