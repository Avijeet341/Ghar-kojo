package com.avi.gharkhojo.Chat

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Adapter.ChatUserListAdapter
import com.avi.gharkhojo.Model.ChatUserListModel
import com.avi.gharkhojo.Model.Message
import com.avi.gharkhojo.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class Chat_Activity : AppCompatActivity() {


   @set:Inject
   var firebaseUser: FirebaseUser? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var binding: ActivityChatBinding
    private var chatUserListModel: ArrayList<ChatUserListModel> = ArrayList()
    @Inject lateinit var firebaseDatabase: FirebaseDatabase
    @Inject lateinit var databaseReference: DatabaseReference
    private var chatUserListAdapter: ChatUserListAdapter? = null
    private var userListListener: ValueEventListener? = null
    private var messageListenerMap: MutableMap<String, ValueEventListener> = mutableMapOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            v.updatePadding(bottom = bottom)
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        chatUserListAdapter = ChatUserListAdapter(chatUserListModel, this)
        recyclerView = binding.recyclerView2
        recyclerView?.adapter = chatUserListAdapter
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.setHasFixedSize(true)


        fetchUsers()
    }

    private fun fetchUsers() {
        userListListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                val userIdSet = HashSet<String>()
                chatUserListModel.clear()
                messageListenerMap.clear()
                for (dataSnapshot in snapshot.children) {
                    val userData = dataSnapshot.getValue(ChatUserListModel::class.java)
                    if (firebaseUser?.uid != userData?.userId && userData?.userId !in userIdSet) {
                        userIdSet.add(userData!!.userId!!)
                        val chatId = firebaseUser!!.uid + userData.userId
                        chatUserListModel.add(userData)
                        addMessageListener(chatId)


                    }
                }


                    chatUserListModel.sortByDescending { it.lastMessageTimestamp ?: 0L }
                    chatUserListAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        databaseReference.addValueEventListener(userListListener!!)
    }

    private fun addMessageListener(chatId: String) {
        if (messageListenerMap.containsKey(chatId)) return

        val messageListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                val userId = chatId.removePrefix(firebaseUser!!.uid)
                val userIndex = chatUserListModel.indexOfFirst { it.userId == userId }
                if (userIndex != -1) {
                    chatUserListModel[userIndex].lastMessageTimestamp = snapshot.children.lastOrNull()?.getValue(Message::class.java)?.timeStamp
                    chatUserListModel[userIndex].lastMessage = snapshot.children.lastOrNull()?.getValue(Message::class.java)?.message
                     chatUserListModel.sortByDescending { it.lastMessageTimestamp ?: 0L }
                         .also {
                             recyclerView?.scrollToPosition(0)
                         }


                    chatUserListAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        val messageRef = firebaseDatabase.reference.child("chats").child(chatId).child("message")
        messageRef.addValueEventListener(messageListener)
        messageListenerMap[chatId] = messageListener
    }

}
