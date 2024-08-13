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
import com.avi.gharkhojo.Adapter.ChatUserListAdapter
import com.avi.gharkhojo.Model.ChatUserListModel
import com.avi.gharkhojo.Model.Message
import com.avi.gharkhojo.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class Chat_Activity : AppCompatActivity() {

   var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
     var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatUserListAdapter: ChatUserListAdapter
    private val chatUserListModel = mutableListOf<ChatUserListModel>()
    private val messageListenerMap = mutableMapOf<String, ValueEventListener>()
    private lateinit var databaseReference: DatabaseReference

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

        setupRecyclerView()
        setupDatabaseReference()
        fetchUsers()
    }

    private fun setupRecyclerView() {
        chatUserListAdapter = ChatUserListAdapter(chatUserListModel, this)
        binding.recyclerView2.apply {
            adapter = chatUserListAdapter
            layoutManager = LinearLayoutManager(this@Chat_Activity)
            setHasFixedSize(true)
        }
    }

    private fun setupDatabaseReference() {
        databaseReference = firebaseDatabase.reference.child("users")
    }

    private fun fetchUsers() {
        val userListListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                val userIdSet = mutableSetOf<String>()
                chatUserListModel.clear()
                messageListenerMap.clear()

                snapshot.children.mapNotNull { it.getValue(ChatUserListModel::class.java) }
                    .filter { it.userId != firebaseUser?.uid && userIdSet.add(it.userId!!) }
                    .forEach { userData ->
                        val chatId = firebaseUser?.uid + userData.userId
                        chatUserListModel.add(userData)
                        addMessageListener(chatId)
                    }

                chatUserListModel.sortByDescending { it.lastMessageTimestamp ?: 0L }
                chatUserListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        databaseReference.addValueEventListener(userListListener)
    }

    private fun addMessageListener(chatId: String) {
        if (messageListenerMap.containsKey(chatId)) return

        val messageListener = object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                val userId = chatId.removePrefix(firebaseUser!!.uid)
                val userIndex = chatUserListModel.indexOfFirst { it.userId == userId }

                if (userIndex != -1) {
                    val lastMessage = snapshot.children.lastOrNull()?.getValue(Message::class.java)
                    chatUserListModel[userIndex].apply {
                        lastMessageTimestamp = lastMessage?.timeStamp
                        this.lastMessage = lastMessage?.message
                    }
                    chatUserListModel.sortByDescending { it.lastMessageTimestamp ?: 0L }
                    chatUserListAdapter.notifyDataSetChanged()
                    binding.recyclerView2.scrollToPosition(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        val messageRef = firebaseDatabase.reference.child("chats").child(chatId).child("message")
        messageRef.addValueEventListener(messageListener)
        messageListenerMap[chatId] = messageListener
    }

}
