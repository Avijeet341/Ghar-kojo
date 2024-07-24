package com.avi.gharkhojo.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Chat.ChatRoom
import com.avi.gharkhojo.Model.ChatUserListModel
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.ActivityChatBinding
import com.avi.gharkhojo.databinding.UserListLayoutBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class ChatUserListAdapter(private val chatUsers: List<ChatUserListModel>,private val context: Context):
    RecyclerView.Adapter<ChatUserListAdapter.ChatUserViewHolder>(){

            class ChatUserViewHolder(userListBinding: UserListLayoutBinding):RecyclerView.ViewHolder(userListBinding.root){

                var img:ImageView = userListBinding.profilePic
                var name: TextView = userListBinding.username
                var time:TextView = userListBinding.msgTime


            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {

        return ChatUserViewHolder(UserListLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return chatUsers.size
    }

    override fun onBindViewHolder(holder: ChatUserViewHolder, position: Int) {

        if (chatUsers.isNotEmpty()) {
            val chatUser: ChatUserListModel = chatUsers[position]
            if (!chatUser.userId.equals(FirebaseAuth.getInstance().currentUser?.uid)) {
                Glide.with(context)
                    .load(Uri.parse((if (!chatUser.userimage.isNullOrEmpty()) chatUser.userimage else R.drawable.baseline_person_24).toString()))
                    .placeholder(R.drawable.baseline_person_24).into(holder.img)
                holder.name.text = chatUser.username

                holder.itemView.setOnClickListener {


                    var intent: Intent = Intent(context, ChatRoom::class.java)
                    intent.putExtra(ChatRoom.IMG_ARG,chatUser.userimage)
                    intent.putExtra(ChatRoom.NAME_ARG,chatUser.username)
                    intent.putExtra(ChatRoom.UID_ARG,chatUser.userId)
                    context.startActivity(intent)
                }
            }

        }



    }


}