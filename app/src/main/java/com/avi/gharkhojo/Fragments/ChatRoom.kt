package com.avi.gharkhojo.Fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Adapter.ChatAdapter
import com.avi.gharkhojo.Model.ChatRoomModel
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.databinding.FragmentChatRoomBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ChatRoom : Fragment() {

    private var img: String? = null
    private var uid: String? = null
    private var name:String? = null

    companion object{
        var IMG_ARG = "img"
        var NAME_ARG = "name"
        var UID_ARG = "uid"
    }


    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatBinding: FragmentChatRoomBinding
    private var chatList: ArrayList<ChatRoomModel> = ArrayList()
    private var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var databaseReference: DatabaseReference = firebaseDatabase.reference.child("users")
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            img = it.getString(IMG_ARG)
            uid = it.getString(UID_ARG)
            name = it.getString(NAME_ARG)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


           Glide.with(this).load(img?:"").into(chatBinding.profileImage)
            chatBinding.name.text = name


        chatBinding = FragmentChatRoomBinding.inflate(layoutInflater)
        recyclerView = chatBinding.recyclerView
        chatAdapter = ChatAdapter(chatList)
        recyclerView.adapter = chatAdapter

        initializeMessaging()

        databaseReference.child("chats").child(firebaseUser!!.uid+uid)
            .addValueEventListener(object:
            ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                for(data in snapshot.children){
                    val chatRoomModel: ChatRoomModel? = data.getValue(ChatRoomModel::class.java)
                    chatList.add(chatRoomModel!!)
                    chatAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,error.message, Toast.LENGTH_SHORT).show()
            }

        })


        return chatBinding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initializeMessaging(){
        if(firebaseUser!=null){

            chatBinding.sendMsg.setOnClickListener{

                val sendMsgText:String = chatBinding.inputMsg.text.toString().trim()
                if(sendMsgText.isNotEmpty()){
                    chatBinding.inputMsg.setText("")
                    val chatRoomModel: ChatRoomModel = ChatRoomModel()
                    with(chatRoomModel){
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                        val currentDateTime = LocalDateTime.now().format(formatter)
                        name =  UserData.username
                        uid = firebaseUser!!.uid
                        msg = sendMsgText
                        time = currentDateTime
                    }
                    databaseReference.child("chats").child(firebaseUser!!.uid+uid)
                        .push().setValue(chatRoomModel)
                }
            }
        }
    }

}