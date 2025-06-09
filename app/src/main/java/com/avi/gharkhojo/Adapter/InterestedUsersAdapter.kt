package com.avi.gharkhojo.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Model.InterestedUser
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.ItemInterestedUserBinding
import com.bumptech.glide.Glide
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Locale

class InterestedUsersAdapter(val onMessageClick:(InterestedUser)->Unit) :
    ListAdapter<InterestedUser, InterestedUsersAdapter.InterestedUserViewHolder>(InterestedUserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestedUserViewHolder {
        val binding = ItemInterestedUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return InterestedUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InterestedUserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
        holder.binding.messageButton.setOnClickListener {
            onMessageClick(user)
        }
    }

    inner class InterestedUserViewHolder(val binding: ItemInterestedUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: InterestedUser) {
            binding.userName.text = user.name

            val formattedDate = formatDate(user.interestedDate)
            val formattedTime = formatTime(user.interestedDate)
            binding.interestedDate.text = "Interested on $formattedDate \tat $formattedTime"

            // Load user image
            user.image?.let {
                Glide.with(binding.root.context)
                    .load(it)
                    .error(R.drawable.baseline_person_24)
                    .circleCrop()
                    .into(binding.userImage)
            } ?: run {
                binding.userImage.setImageResource(R.drawable.kk)
            }

        }

        private fun formatDate(dateString: String): String {
            return try {
                var formate = SimpleDateFormat("dd-MM-yyy", Locale.getDefault())
                formate.format(Date(dateString.toLong()))
            } catch (e: Exception) {
                dateString
            }.toString()

        }
    }
    private fun formatTime(dateString: String):String{
        return try{
            var formate = SimpleDateFormat("hh:mm a",Locale.getDefault())
            formate.format(Time(dateString.toLong()))
        }catch (e: Exception){
            e.message
        }.toString()
    }
    fun updateList(newList: List<InterestedUser>) {
        submitList(newList)
    }
}

class InterestedUserDiffCallback : androidx.recyclerview.widget.DiffUtil.ItemCallback<InterestedUser>() {
    override fun areItemsTheSame(oldItem: InterestedUser, newItem: InterestedUser): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: InterestedUser, newItem: InterestedUser): Boolean {
        return oldItem == newItem
    }


}
