package com.avi.gharkhojo.Adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.avi.gharkhojo.Chat.ChatRoom
import com.avi.gharkhojo.Model.AndroidUtils
import com.avi.gharkhojo.Model.Message
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.DeleteLayoutBinding
import com.avi.gharkhojo.databinding.ReceiverMsgBinding
import com.avi.gharkhojo.databinding.SenderMsgBinding
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MessageAdapter(
    var context:Context,
    messages:ArrayList<Message>?,
    senderRoom:String?,
    receiverRoom:String?,
    receiverName: String?,
    scrollTo:ScrollTo?
) :RecyclerView.Adapter<RecyclerView.ViewHolder?>(){
    lateinit var messages:ArrayList<Message>
    val ITEM_SENT = 1
    val ITEM_RECEIVE = 2
    var senderRoom:String? = null
    var receiverName:String? = null
    var receiverRoom:String? = null
    var storageRef:StorageReference = FirebaseStorage.getInstance().reference
    var scrollTo:ScrollTo?=null

    init {
        if(messages!=null)
        {
            this.messages = messages
            this.senderRoom = senderRoom
            this.receiverRoom = receiverRoom
            this.receiverName = receiverName
            this.scrollTo = scrollTo
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  if(viewType == ITEM_SENT)
        {
            val view:View = LayoutInflater.from(context).inflate(R.layout.sender_msg,parent,false)
            SentMsgHolder(view)
        }
        else{
            val view:View = LayoutInflater.from(context).inflate(R.layout.receiver_msg,parent,false)
            ReceiveMsgHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message:Message = messages.get(position)
        return if(FirebaseAuth.getInstance().currentUser?.uid.equals(message.senderId)){
            ITEM_SENT
        }
        else{
            ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int = messages.size

    private fun autoSlider(viewPager2: ViewPager2,imgList: List<String>, handler: Handler): Runnable{
        return object : Runnable{
            override fun run() {
                var currentItem = viewPager2.currentItem
                var nextItem = (currentItem + 1) % imgList.size
                viewPager2.currentItem = nextItem
                handler.postDelayed(this,3000)
            }

        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]


        if (holder.javaClass == SentMsgHolder::class.java) {
            val viewHolder = holder as SentMsgHolder
            viewHolder.binding.replyLayout.visibility = View.GONE
            viewHolder.binding.photos.visibility = View.GONE
            viewHolder.binding.mLinear.visibility = View.VISIBLE
            viewHolder.binding.senderTextMsg.visibility = View.VISIBLE

            if (message.isImage) {
                viewHolder.binding.imgCounter.visibility = View.VISIBLE
                viewHolder.binding.imgCounter.text = ""
                viewHolder.binding.photos.visibility = View.VISIBLE
                viewHolder.binding.mLinear.visibility = View.GONE
                viewHolder.binding.senderTextMsg.visibility = View.GONE
                var photoAdapter = PhotoAdapter(message.imageUrl?.values?.toList()?:listOf())
                viewHolder.binding.photoViewPager.adapter = photoAdapter
                viewHolder.binding.photoViewPager.setPageTransformer(AndroidUtils.getTransformation())
                var handler: Handler = Handler(Looper.getMainLooper())
                handler.post(autoSlider(viewHolder.binding.photoViewPager,message.imageUrl?.values?.toList()?:listOf(),handler))
                viewHolder.binding.photoViewPager.registerOnPageChangeCallback(object:ViewPager2.OnPageChangeCallback(){
                    override fun onPageSelected(pos: Int) {
                        if(photoAdapter.itemCount == 1){
                            viewHolder.binding.imgCounter.visibility = View.GONE
                        }else{
                            viewHolder.binding.imgCounter.text = "${pos+1}/${photoAdapter.itemCount}"
                        }
                    }
                })

//                Glide.with(context)
//                    .load(message.imageUrl)
//                    .placeholder(R.drawable.image_placeholder)
//                    .into(viewHolder.binding.image)
            }

                viewHolder.binding.senderTextMsg.text = message.message
            message.repliedMsg?.let {
                viewHolder.binding.repliedMessg.text = message.repliedMsg
                if(message.replyToId == FirebaseAuth.getInstance().currentUser?.uid){
                    viewHolder.binding.repliedName.text = "You"
                }
                else{
                    viewHolder.binding.repliedName.text = receiverName
                }
                viewHolder.binding.replyLayout.visibility = View.VISIBLE


            }

            viewHolder.binding.replyLayout.setOnClickListener{
                if(message.repliedMsgPosition != null &&  message.repliedMsgPosition!!<messages.size
                    && message.repliedMsg == messages[message.repliedMsgPosition!!].message)
                    scrollTo?.ScrollToRepliedMessage(message.repliedMsgPosition!!)
            }


            viewHolder.binding.time.text = formatDate(message.timeStamp)
            viewHolder.itemView.setOnLongClickListener {
                val view = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
                val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)
                val dialog = androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setView(binding.root)
                    .create()
                binding.everyone.setOnClickListener {

                  var senderMsgDel =   message.messageId?.let {
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom!!)
                            .child("message")
                            .child(it).setValue(null)

                    }
                var receiverMsgDel =    message.messageId?.let {

                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(receiverRoom!!)
                            .child("message")
                            .child(it).setValue(null)

                    }

                    if(message.messageId!=null) {
                        Tasks.whenAllComplete(senderMsgDel, receiverMsgDel).addOnSuccessListener {
                                    ChatRoom.DeleteFolders.deleteFolder(FirebaseStorage.getInstance().reference.child("chats").child(senderRoom.toString())
                                        .child(message.timeStamp.toString()))
                                    ChatRoom.DeleteFolders.deleteFolder(FirebaseStorage.getInstance().reference.child("chats").child(receiverRoom.toString())
                                        .child(message.timeStamp.toString()))

                        }

                    }


                    if (position == messages.size - 1) {

                        var senderLastMsg:String?=null
                        var senderLastMsgTime:String?=null

                        var receiverLastMsg:String?=null
                        var receiverLastMsgTime:String?=null

                      var senderTask = FirebaseDatabase.getInstance().reference.child("chats")
                                .child(senderRoom!!)
                                .child("message")
                                .get().addOnSuccessListener {
                                    if(it.children.count()>0 && it.children.last().getValue(Message::class.java)!=null) {
                                        senderLastMsg = it.children.last()
                                            .getValue(Message::class.java)?.message
                                        senderLastMsgTime = it.children.last()
                                            .getValue(Message::class.java)?.timeStamp.toString()
                                    }
                                }



                    var receiverTask = FirebaseDatabase.getInstance().reference.child("chats")
                                .child(receiverRoom!!)
                                .child("message")
                                .get().addOnSuccessListener {

                                    if(it.children.count()>0 && it.children.last().getValue(Message::class.java)!=null) {
                                        receiverLastMsg = it.children.last()
                                            .getValue(Message::class.java)?.message
                                        receiverLastMsgTime = it.children.last()
                                            .getValue(Message::class.java)?.timeStamp.toString()
                                    }
                                }



                        Tasks.whenAllComplete(senderTask,receiverTask).addOnSuccessListener {

                            FirebaseDatabase.getInstance().reference.child("chats")
                                .child(senderRoom!!)
                                .child("lastMsg").setValue(senderLastMsg)

                            FirebaseDatabase.getInstance().reference.child("chats")
                                .child(senderRoom!!)
                                .child("lastMsgTime").setValue(senderLastMsgTime)


                            FirebaseDatabase.getInstance().reference.child("chats")
                                .child(receiverRoom!!)
                                .child("lastMsg").setValue(receiverLastMsg)

                            FirebaseDatabase.getInstance().reference.child("chats")
                                .child(receiverRoom!!)
                                .child("lastMsgTime").setValue(receiverLastMsgTime)
                        }




                    }



                    dialog.dismiss()
                }
                binding.delete.setOnClickListener {
                   var senderMsgDel =   message.messageId?.let {
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom!!)
                            .child("message")
                            .child(it).setValue(null)
                    }

                    if(message.messageId!=null) {
                        Tasks.whenAllComplete(senderMsgDel).addOnSuccessListener {
                                    ChatRoom.DeleteFolders.deleteFolder(FirebaseStorage.getInstance().reference.child("chats").child(senderRoom.toString())
                                        .child(message.timeStamp.toString()))


                        }

                    }


                    if(position == messages.size-1){
                        var senderLastMsg:String?=null
                        var senderLastMsgTime:String?=null

                      var senderTask = FirebaseDatabase.getInstance().reference.child("chats")
                                .child(senderRoom!!)
                                .child("message")
                                .get().addOnSuccessListener {
                              if (it.children.count()>0 && it.children.last()
                                      .getValue(Message::class.java) != null
                              ) {
                                  senderLastMsg =
                                      it.children.last().getValue(Message::class.java)?.message
                                  senderLastMsgTime = it.children.last()
                                      .getValue(Message::class.java)?.timeStamp.toString()
                              }
                          }


                        Tasks.whenAllComplete(senderTask).addOnSuccessListener {
                            FirebaseDatabase.getInstance().reference.child("chats")
                                .child(senderRoom!!)
                                .child("lastMsg").setValue(senderLastMsg)

                            FirebaseDatabase.getInstance().reference.child("chats")
                                .child(senderRoom!!)
                                .child("lastMsgTime").setValue(senderLastMsgTime)
                        }
                        }




                    dialog.dismiss()

                }
                binding.cancel.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                false
            }

        } else {
            val viewHolder = holder as ReceiveMsgHolder

            viewHolder.binding.replyLayout.visibility = View.GONE
            viewHolder.binding.photos.visibility = View.GONE
            viewHolder.binding.mLinear.visibility = View.VISIBLE
            viewHolder.binding.receiverTxtMsg.visibility = View.VISIBLE

            if (message.isImage) {
                viewHolder.binding.imgCounter.visibility = View.VISIBLE
                viewHolder.binding.imgCounter.text = ""
                viewHolder.binding.photos.visibility = View.VISIBLE
                viewHolder.binding.mLinear.visibility = View.GONE
                viewHolder.binding.receiverTxtMsg.visibility = View.GONE

                var photoAdapter = PhotoAdapter(message.imageUrl?.values?.toList()?:listOf())
                viewHolder.binding.photoViewPager.adapter = photoAdapter
                viewHolder.binding.photoViewPager.setPageTransformer(AndroidUtils.getTransformation())
                var handler: Handler = Handler(Looper.getMainLooper())
                handler.post(autoSlider(viewHolder.binding.photoViewPager,message.imageUrl?.values?.toList()?:listOf(),handler))
                viewHolder.binding.photoViewPager.registerOnPageChangeCallback(object:ViewPager2.OnPageChangeCallback(){
                    override fun onPageSelected(pos: Int) {
                        if(photoAdapter.itemCount == 1){
                            viewHolder.binding.imgCounter.visibility = View.GONE
                        }else{
                            viewHolder.binding.imgCounter.text = "${pos+1}/${photoAdapter.itemCount}"

                        }
                    }
                })
//                Glide.with(context)
//                    .load(message.imageUrl)
//                    .placeholder(R.drawable.image_placeholder)
//                    .into(viewHolder.binding.photos)


            }

                viewHolder.binding.receiverTxtMsg.text = message.message

            message.repliedMsg?.let {
                viewHolder.binding.repliedMessg.text = message.repliedMsg
                if(message.replyToId == FirebaseAuth.getInstance().currentUser?.uid){
                    viewHolder.binding.repliedName.text = "You"
                }
                else{
                    viewHolder.binding.repliedName.text = receiverName
                }
                viewHolder.binding.replyLayout.visibility = View.VISIBLE
            }
                viewHolder.binding.replyLayout.setOnClickListener{
                    if(message.repliedMsgPosition != null &&  message.repliedMsgPosition!!<messages.size
                        && message.repliedMsg == messages[message.repliedMsgPosition!!].message)
                        scrollTo?.ScrollToRepliedMessage(message.repliedMsgPosition!!)
                }

            viewHolder.binding.time.text = formatDate(message.timeStamp)
            viewHolder.itemView.setOnLongClickListener{
                val view = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
                val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)
                binding.everyone.visibility = View.GONE
                val dialog = androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle("Delete Message")
                    .setView(binding.root)
                    .create()

                binding.delete.setOnClickListener {
                   var senderMsgDel = message.messageId?.let {
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom!!)
                            .child("message")
                            .child(it).setValue(null)
                    }

                    if(message.messageId!=null){
                        Tasks.whenAllComplete(senderMsgDel).addOnSuccessListener {
                                   ChatRoom.DeleteFolders.deleteFolder(FirebaseStorage.getInstance().reference.child("chats").child(senderRoom.toString())
                                       .child(message.timeStamp.toString()))

                        }
                    }



                    if(position == messages.size-1){
                        var senderLastMsg:String?=null
                        var senderLastMsgTime:String?=null

                        var senderTask = FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom!!)
                            .child("message")
                            .get().addOnSuccessListener {
                                if (it.children.count()>0 && it.children.last()
                                        .getValue(Message::class.java) != null
                                ) {
                                    senderLastMsg =
                                        it.children.last().getValue(Message::class.java)?.message
                                    senderLastMsgTime = it.children.last()
                                        .getValue(Message::class.java)?.timeStamp.toString()
                                }
                            }


                        Tasks.whenAllComplete(senderTask).addOnSuccessListener {
                            FirebaseDatabase.getInstance().reference.child("chats")
                                .child(senderRoom!!)
                                .child("lastMsg").setValue(senderLastMsg)

                            FirebaseDatabase.getInstance().reference.child("chats")
                                .child(senderRoom!!)
                                .child("lastMsgTime").setValue(senderLastMsgTime)
                        }
                    }

                    dialog.dismiss()

                }
                binding.cancel.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                false
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

    inner class SentMsgHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var binding:SenderMsgBinding = SenderMsgBinding.bind(itemView)

    }

    inner class ReceiveMsgHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var binding: ReceiverMsgBinding = ReceiverMsgBinding.bind(itemView)

    }

    interface ScrollTo{
        fun ScrollToRepliedMessage(position: Int)
    }
}