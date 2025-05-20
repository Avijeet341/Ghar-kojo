package com.avi.gharkhojo.Adapter

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Model.Post
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.DeleteDialogBinding
import com.avi.gharkhojo.databinding.OwnerUploadsItemBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class UploadsAdapter(private val onItemClick: (Post) -> Unit,private val onDelete: (Post,dialog:Dialog?) -> Unit) :
    RecyclerView.Adapter<UploadsAdapter.UploadViewHolder>() {
    private val uploads = ArrayList<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadViewHolder {



        val binding = OwnerUploadsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UploadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UploadViewHolder, position: Int) {
        holder.bind(uploads[position])
    }

    override fun getItemCount(): Int = uploads.size

    fun updateData(newUploads: MutableList<Post>) {
        this.uploads.clear()
        this.uploads.addAll(newUploads)
        notifyDataSetChanged()
    }

    inner class UploadViewHolder(private val binding: OwnerUploadsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            Glide.with(binding.root.context)
                .load(post.coverImage.orEmpty())
                .error(R.drawable.home_icon)
                .into(binding.houseImage)

            binding.houseTitle.text = post.propertyType.orEmpty()
            binding.houseLocation.text = "${post.area.orEmpty()}, ${post.city.orEmpty()}"
            binding.housePriceAmount.text = "${post.rent}"
            binding.housePricePeriod.text = "/Month"
            binding.houseBedrooms.text = post.noOfBedRoom.toString()
            binding.houseBathrooms.text = post.noOfBathroom.toString()
            binding.houseArea.text = "${post.builtUpArea} sq.ft."

            binding.root.setOnClickListener {
                onItemClick(post)
            }
            if(post.userId!=FirebaseAuth.getInstance().currentUser?.uid){
                binding.btnDelete.visibility = View.GONE
            }
            binding.btnDelete.setOnClickListener {
                val dialogBinding = DeleteDialogBinding.inflate(LayoutInflater.from(binding.root.context))
                val dialog = AlertDialog.Builder(binding.root.context)
                    .setView(dialogBinding.root)
                    .setCancelable(false)
                    .create()

                dialogBinding.dialogButtonNo.setOnClickListener {
                    dialog.dismiss()
                }

                dialogBinding.dialogButtonYes.setOnClickListener {
                    onDelete(post, dialog)
                }

                dialog.show()
            }

        }
    }
}
