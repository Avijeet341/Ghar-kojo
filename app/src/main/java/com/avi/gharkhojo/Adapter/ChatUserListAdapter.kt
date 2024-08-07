package com.avi.gharkhojo.Adapter

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Chat.ChatRoom
import com.avi.gharkhojo.Model.ChatUserListModel
import com.avi.gharkhojo.Model.Message
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.ActivityChatBinding
import com.avi.gharkhojo.databinding.UserListLayoutBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ChatUserListAdapter(private val chatUsers: List<ChatUserListModel>,private val context: Context):
    RecyclerView.Adapter<ChatUserListAdapter.ChatUserViewHolder>(){

            class ChatUserViewHolder(userListBinding: UserListLayoutBinding):RecyclerView.ViewHolder(userListBinding.root){

                var img:ImageView = userListBinding.profilePic
                var name: TextView = userListBinding.username
                var time:TextView = userListBinding.msgTime
                var lastMsg:TextView = userListBinding.lastMsg


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
                    .load(chatUser.userimage)
                    .placeholder(R.drawable.baseline_person_24).into(holder.img)
                holder.name.text = chatUser.username
                holder.time.text = chatUser.lastMessageTimestamp?.let { formatDate(it) }
                holder.lastMsg.text = chatUser.lastMessage



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

    private fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        val now = Calendar.getInstance()

        val format = when {
            calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                    calendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR) -> {
                SimpleDateFormat("h:mm a", Locale.getDefault())
            }
            calendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) -> {
                SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
            }
            else -> {
                SimpleDateFormat("MMM d yyyy, h:mm a", Locale.getDefault())
            }
        }

        return format.format(calendar.time)
    }


}