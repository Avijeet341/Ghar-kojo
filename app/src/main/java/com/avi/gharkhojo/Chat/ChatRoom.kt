package com.avi.gharkhojo.Chat

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Adapter.MessageAdapter
import com.avi.gharkhojo.Model.Message
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.ActivityChatRoomBinding

import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.logging.Handler

class ChatRoom : AppCompatActivity() {

    private var img: String? = null
    private var name:String? = null
    private var senderRoom:String? = null
    private var receiverRoom:String? = null
    var storage:FirebaseStorage? = null
    var dialog:ProgressDialog? = null
    var receiverUid:String? = null
    var senderUid:String? = null


    companion object{
        var IMG_ARG = "image"
        var NAME_ARG = "name"
        var UID_ARG = "uid"
    }


    private lateinit var chatAdapter: MessageAdapter
    private lateinit var chatBinding: ActivityChatRoomBinding
    private var messages: ArrayList<Message> = ArrayList()
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference = firebaseDatabase.reference
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private lateinit var recyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        chatBinding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(chatBinding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        ViewCompat.setOnApplyWindowInsetsListener(chatBinding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setSupportActionBar(chatBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        chatBinding.toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_ios_new_24)
        chatBinding.toolbar.setNavigationOnClickListener {
            finish()
        }


//        chatBinding.inputMsg.requestFocus()
        storage = FirebaseStorage.getInstance()
        dialog = ProgressDialog(this)
        dialog!!.setMessage("Uploading Image...")
        dialog!!.setCancelable(false)
        dialog!!.setCanceledOnTouchOutside(false)

        intent.extras?.let {
            img = it.getString(IMG_ARG)
            receiverUid = it.getString(UID_ARG)
            name = it.getString(NAME_ARG)
            senderUid = firebaseUser!!.uid

        }


        Glide.with(this).load(img).placeholder(R.drawable.baseline_person_24).into(chatBinding.profileImage)
        chatBinding.name.text = name

        databaseReference.child("Presence").child(receiverUid!!)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val status = snapshot.getValue(String::class.java)
                        if(status == "Offline"){
                            chatBinding.status.visibility = View.GONE
                        }
                        else{
                            chatBinding.status.text = status
                            chatBinding.status.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        senderRoom = senderUid+receiverUid
        receiverRoom = receiverUid+senderUid
        chatAdapter = MessageAdapter(this,messages,senderRoom,receiverRoom)

        recyclerView = chatBinding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = chatAdapter


        databaseReference.child("chats")
            .child(senderRoom!!)
            .child("message")
            .addValueEventListener(object:
                ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages.clear()
                    for(data in snapshot.children){
                        val message: Message? = data.getValue(Message::class.java)
                        message?.messageId = data.key
                        messages.add(message!!)

                    }
                    chatAdapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(messages.size-1)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ChatRoom,error.message, Toast.LENGTH_SHORT).show()
                }

            })


        initializeMessaging()


    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeMessaging(){
        if(firebaseUser!=null){

            chatBinding.sendMsg.setOnClickListener{

                val sendMsgText:String = chatBinding.inputMsg.text.toString().trim()
                val date = Date()
                val message = Message(sendMsgText,firebaseUser!!.uid,date.time)
                if(sendMsgText.isNotEmpty()){
                    chatBinding.inputMsg.setText("")

                    val randomKey = databaseReference.push().key
                    val lastMsgObj = HashMap<String,Any>()
                    lastMsgObj["lastMsg"] = message.message!!
                    lastMsgObj["lastMsgTime"] = date.time
                    databaseReference.child("chats").child(senderRoom!!).updateChildren(lastMsgObj)
                    databaseReference.child("chats").child(receiverRoom!!).updateChildren(lastMsgObj)
                    databaseReference.child("chats").child(senderRoom!!).child("message").child(randomKey!!)
                        .setValue(message).addOnSuccessListener {
                            databaseReference.child("chats").child(receiverRoom!!).child("message").child(randomKey)
                                .setValue(message).addOnSuccessListener {

                                }

                        }


                }
            }

            chatBinding.attachment.setOnClickListener{
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                startActivityForResult(intent,1)
            }

            val handler = android.os.Handler()
            chatBinding.inputMsg.addTextChangedListener(object:TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    databaseReference.child("Presence")
                        .child(senderUid!!)
                        .setValue("typing...")
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed(userStoppedTyping, 1000)

                }
                var userStoppedTyping = Runnable {
                    databaseReference.child("Presence")
                        .child(senderUid!!)
                        .setValue("Online")
                }

            })

            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.data != null) {
                    val selectedImage = data.data
                    val calendar = Calendar.getInstance()
                    val img_id = calendar.timeInMillis.toString()
                    val ref = storage!!.reference.child("chats")
                        .child(img_id)
                    dialog?.show()
                    ref.putFile(selectedImage!!).addOnCompleteListener { task ->
                        dialog?.dismiss()
                        if (task.isSuccessful) {
                            ref.downloadUrl.addOnSuccessListener { uri ->
                                val filePath = uri.toString()
//                                val messageTxt: String = chatBinding.inputMsg.text.toString().trim()
                                val date = Date()
                                val message = Message(null, firebaseUser!!.uid, date.time)
                                message.imageUrl = filePath
                                message.message = "photos"
                                message.isImage = true
                                chatBinding.inputMsg.setText("")
                                val randomKey = databaseReference.push().key
                                val lastMsgObj = HashMap<String, Any>()
                                lastMsgObj["lastMsg"] = message.message!!
                                lastMsgObj["lastMsgTime"] = date.time
                                message.img_id = img_id
                                databaseReference.child("chats").child(senderRoom!!)
                                    .updateChildren(lastMsgObj)
                                databaseReference.child("chats").child(receiverRoom!!)
                                    .updateChildren(lastMsgObj)
                                databaseReference.child("chats").child(senderRoom!!)
                                    .child("message").child(randomKey!!)
                                    .setValue(message).addOnSuccessListener {
                                        databaseReference.child("chats").child(receiverRoom!!)
                                            .child("message").child(randomKey)
                                            .setValue(message).addOnSuccessListener {

                                            }


                                    }

                            }

                        }
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        var currentId = FirebaseAuth.getInstance().uid
        databaseReference.child("Presence")
            .child(currentId!!)
            .setValue("Online")

    }
    override fun onPause() {
        super.onPause()
        var currentId = FirebaseAuth.getInstance().uid
        databaseReference.child("Presence")
            .child(currentId!!)
            .setValue("Offline")

    }

}