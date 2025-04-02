package com.avi.gharkhojo.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Model.InterestedUser
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.ItemInterestedUserBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class InterestedUsersAdapter :
    ListAdapter<InterestedUser, InterestedUsersAdapter.InterestedUserViewHolder>(InterestedUserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestedUserViewHolder {
        val binding = ItemInterestedUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InterestedUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InterestedUserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    inner class InterestedUserViewHolder(private val binding: ItemInterestedUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: InterestedUser) {
            binding.userName.text = user.name

            // Format the date string
            val formattedDate = formatDate(user.interestedDate)
            binding.interestedDate.text = "Interested on $formattedDate"

            // Load user image
            user.image?.let {
                Glide.with(binding.root.context)
                    .load(it)
                    .circleCrop()
                    .into(binding.userImage)
            } ?: run {
                binding.userImage.setImageResource(R.drawable.kk)
            }
        }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
                val date = inputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: dateString
            } catch (e: Exception) {
                dateString // Return original string if parsing fails
            }
        }
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
