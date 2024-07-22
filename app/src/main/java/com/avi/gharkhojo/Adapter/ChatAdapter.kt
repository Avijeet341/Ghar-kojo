package com.avi.gharkhojo.Adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.avi.gharkhojo.Model.ChatRoomModel
import com.avi.gharkhojo.databinding.ReceiverBinding
import com.avi.gharkhojo.databinding.SenderBinding
import com.google.firebase.auth.FirebaseAuth

class ChatAdapter(private val list: List<ChatRoomModel>): RecyclerView.Adapter<ViewHolder>() {

    private val firebaseUser = FirebaseAuth.getInstance().currentUser


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return if (viewType == 1) {
            SenderChatViewHolder(SenderBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            ReceiverChatViewHolder(ReceiverBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun getItemCount(): Int {

        return list.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatModel = list[position]

        if (holder is SenderChatViewHolder) {
            holder.msg_text.text = chatModel.msg
            holder.time.text = chatModel.time
            return
        }
        if (holder is ReceiverChatViewHolder) {
            holder.msg_text.text = chatModel.msg
            holder.time.text = chatModel.time
        }
    }

    override fun getItemViewType(position: Int): Int {

        if (firebaseUser?.uid == list[position].uid) {
            return 1
        } else {
            return 2
        }
    }

    class SenderChatViewHolder(senderBinding: SenderBinding): ViewHolder(senderBinding.root) {

        var msg_text: TextView = senderBinding.senderTextMsg
        var time: TextView = senderBinding.time



    }

    class ReceiverChatViewHolder(receiverBinding: ReceiverBinding): ViewHolder(receiverBinding.root) {
        var msg_text: TextView = receiverBinding.receiverTxtMsg
        var time: TextView = receiverBinding.time
    }
}