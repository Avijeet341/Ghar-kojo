package com.avi.gharkhojo.Chat

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Ringtone
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Adapter.MessageAdapter
import com.avi.gharkhojo.Adapter.MessageSwipeController
import com.avi.gharkhojo.Model.Message
import com.avi.gharkhojo.Model.SwipeControllerActions
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
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ChatRoom : AppCompatActivity() {

    private var senderUid: String?=null
    private var reply_pos: Int?=null
    private var replyToId: String? = null
    private var img: String? = null
    private var name: String? = null
    private var senderRoom: String? = null
    private var receiverRoom: String? = null
    var storage: FirebaseStorage? = null
    var dialog: ProgressDialog? = null
    var receiverUid: String? = null

    private lateinit var currentPhotoPath: String

    companion object {
        var IMG_ARG = "image"
        var NAME_ARG = "name"
        var UID_ARG = "uid"
    }

    private lateinit var chatAdapter: MessageAdapter
    private lateinit var chatBinding: ActivityChatRoomBinding
    private var messages: ArrayList<Message> = ArrayList()
    var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    var databaseReference: DatabaseReference = firebaseDatabase.reference
     var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private lateinit var recyclerView: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        chatBinding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(chatBinding.root)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        ViewCompat.setOnApplyWindowInsetsListener(chatBinding.root) { v, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())




            chatBinding.toolbar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = statusBar.top
            }
            v.updatePadding(bottom = imeInsets.bottom)
            insets
        }

        setSupportActionBar(chatBinding.toolbar)

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
            senderRoom = senderUid + receiverUid
            receiverRoom = receiverUid + senderUid
        }

        Glide.with(this).load(img).placeholder(R.drawable.baseline_person_24).into(chatBinding.profileImage)
        chatBinding.name.text = name

        databaseReference.child("Presence").child(receiverRoom!!).child(receiverUid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val status = snapshot.getValue(String::class.java)
                        if (status == "Offline") {
                            chatBinding.status.text = status
                            chatBinding.status.visibility = View.INVISIBLE
                        } else {
                            chatBinding.status.text = status
                            chatBinding.status.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ChatRoom, error.message, Toast.LENGTH_SHORT).show()
                }
            })

        chatAdapter = MessageAdapter(this, messages, senderRoom, receiverRoom,name,object:MessageAdapter.ScrollTo{
            override fun ScrollToRepliedMessage(position: Int) {
                recyclerView.scrollToPosition(position)
            }

        })

        recyclerView = chatBinding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = chatAdapter

        databaseReference.child("chats")
            .child(senderRoom!!)
            .child("message")
            .addValueEventListener(object :
                ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messages.clear()
                    for (data in snapshot.children) {
                        val message: Message? = data.getValue(Message::class.java)
                        message?.messageId = data.key
                        messages.add(message!!)
                    }

                    chatAdapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(messages.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ChatRoom, error.message, Toast.LENGTH_SHORT).show()
                }

            })

        val messageSwipeController = MessageSwipeController(this, object : SwipeControllerActions {
            override fun showReplyUI(position: Int) {
                showQuotedMessage(messages[position])
                this@ChatRoom.reply_pos = position
                this@ChatRoom.replyToId = messages[position].senderId
            }
        })

        val itemTouchHelper = ItemTouchHelper(messageSwipeController)
        itemTouchHelper.attachToRecyclerView(recyclerView)

       chatBinding.cancelButton.setOnClickListener {
            hideReplyLayout()
        }
        initializeMessaging()
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeMessaging() {
        if (firebaseUser != null) {

            chatBinding.sendMsg.setOnClickListener {
                val sendMsgText: String = chatBinding.inputMsg.text.toString().trim()
                val date = Date()
                val message = Message(sendMsgText, firebaseUser!!.uid, date.time)
                var reply:String? = null
                if(chatBinding.replyLayout.visibility==View.VISIBLE){
                    reply = chatBinding.txtQuotedMsg.text.toString()
                    message.repliedMsgPosition = reply_pos
                    message.replyToId = replyToId
                    hideReplyLayout()
                }
                if (sendMsgText.isNotEmpty()) {
                    chatBinding.inputMsg.setText("")

                    if(reply!=null){
                        message.repliedMsg = reply
                    }
                    message.senderId = senderUid

                    val randomKey = databaseReference.push().key
                    val lastMsgObj = HashMap<String, Any>()
                    lastMsgObj["lastMsg"] = message.message!!
                    lastMsgObj["lastMsgTime"] = date.time
                    databaseReference.child("chats").child(senderRoom!!).updateChildren(lastMsgObj)
                    databaseReference.child("chats").child(receiverRoom!!).updateChildren(lastMsgObj)
                    databaseReference.child("chats").child(senderRoom!!).child("message").child(randomKey!!)
                        .setValue(message)
                    databaseReference.child("chats").child(receiverRoom!!).child("message").child(randomKey)
                        .setValue(message)

                }
            }

            chatBinding.attachment.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                startActivityForResult(intent, 1)
            }
            chatBinding.camera.setOnClickListener {
                if (checkPermission()) {
                    openCamera()
                } else {
                    requestPermission()
                }
            }

            val handler = android.os.Handler()
            chatBinding.inputMsg.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    databaseReference.child("Presence").child(senderRoom!!)
                        .child(senderUid!!)
                        .setValue("typing...")
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed(userStoppedTyping, 1000)
                }

                var userStoppedTyping = Runnable {
                    databaseReference.child("Presence").child(senderRoom!!)
                        .child(senderUid!!)
                        .setValue("Online")
                }
            })

            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }



    private fun checkPermission(): Boolean {
        val camera = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
        val storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        return camera == PackageManager.PERMISSION_GRANTED && storage == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this@ChatRoom,
            arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE),
            100
        )
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
                null
            }

            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "com.avi.gharkhojo.fileprovider",
                    it
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, 2)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {

            currentPhotoPath = absolutePath
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            uploadImage(selectedImage)
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            val file = File(currentPhotoPath)
            val selectedImage = FileProvider.getUriForFile(
                this,
                "com.avi.gharkhojo.fileprovider",
                file
            )
            uploadImage(selectedImage)
        }
    }

    private fun uploadImage(selectedImage: Uri?) {
        selectedImage?.let {
            val calendar = Calendar.getInstance()
            val imgId = calendar.timeInMillis.toString()
            val ref = storage!!.reference.child("chats").child(imgId)
            dialog?.show()
            ref.putFile(it).addOnCompleteListener { task ->
                dialog?.dismiss()
                if (task.isSuccessful) {
                    ref.downloadUrl.addOnSuccessListener { uri ->
                        val filePath = uri.toString()
                        val date = Date()
                        val message = Message(null, firebaseUser!!.uid, date.time).apply {
                            imageUrl = filePath
                            this.message = "photos"
                            isImage = true
                            img_id = imgId
                        }
                        chatBinding.inputMsg.setText("")
                        val randomKey = databaseReference.push().key
                        val lastMsgObj = HashMap<String, Any>().apply {
                            put("lastMsg", message.message!!)
                            put("lastMsgTime", date.time)
                        }
                        databaseReference.child("chats").child(senderRoom!!)
                            .updateChildren(lastMsgObj)
                        databaseReference.child("chats").child(receiverRoom!!)
                            .updateChildren(lastMsgObj)
                        databaseReference.child("chats").child(senderRoom!!)
                            .child("message").child(randomKey!!)
                            .setValue(message).addOnSuccessListener {
                                databaseReference.child("chats").child(receiverRoom!!)
                                    .child("message").child(randomKey)
                                    .setValue(message)
                            }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                }
            }
        }
    }


    private fun hideReplyLayout() {
        chatBinding.replyLayout.visibility = View.GONE
        this@ChatRoom.reply_pos = null
        this@ChatRoom.replyToId = null
    }

    private fun showQuotedMessage(message: Message) {
        chatBinding.inputMsg.requestFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager?.showSoftInput(chatBinding.inputMsg, InputMethodManager.SHOW_IMPLICIT)
        chatBinding.txtQuotedMsg.text = message.message
        if(message.senderId!=firebaseUser!!.uid) {
            chatBinding.quotedName.text = name
        }
        else{
            chatBinding.quotedName.text = "You"
        }
        chatBinding.replyLayout.visibility = View.VISIBLE

    }



    override fun onResume() {
        super.onResume()
        databaseReference.child("Presence").child(senderRoom!!)
            .child(senderUid!!)
            .setValue("Online")
    }

    override fun onPause() {
        super.onPause()

        databaseReference.child("Presence").child(senderRoom!!)
            .child(senderUid!!)
            .setValue("Offline")
    }


}
