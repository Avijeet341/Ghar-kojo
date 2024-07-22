package com.avi.gharkhojo.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Model.ChatUserListModel
import com.avi.gharkhojo.databinding.ActivityChatBinding
import com.avi.gharkhojo.databinding.UserListLayoutBinding
import com.bumptech.glide.Glide

class ChatUserListAdapter(private val chatUsers: List<ChatUserListModel>,private val context: Context):
    RecyclerView.Adapter<ChatUserListAdapter.ChatUserViewHolder>(){

            class ChatUserViewHolder(userListBinding: UserListLayoutBinding):RecyclerView.ViewHolder(userListBinding.root){

                var img:ImageView = userListBinding.profilePic
                var name: TextView = userListBinding.username
                var time:TextView = userListBinding.msgTime


            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {

        return ChatUserViewHolder(UserListLayoutBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return chatUsers.size
    }

    override fun onBindViewHolder(holder: ChatUserViewHolder, position: Int) {

        val chatUser = chatUsers[position]
        Glide.with(context).load(Uri.parse(chatUser.userimage)).into(holder.img)
        holder.name.text = chatUser.username

    }


}