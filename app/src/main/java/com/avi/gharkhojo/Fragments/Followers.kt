package com.avi.gharkhojo.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Adapter.FollowersAdapter
import com.avi.gharkhojo.Chat.ChatRoom
import com.avi.gharkhojo.Model.ChatUserListModel
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentFollowersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Followers : Fragment() {

    var _binding:FragmentFollowersBinding? = null
    val binding get() = _binding!!
    val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    var uid:String? = null
    companion object{
        val UID:String = "FOLLOWER_UID"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       _binding = FragmentFollowersBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        arguments?.let {
            uid = it.getString(UID)
        }
        setUpAdapterWithRecycleView()
        fetcData()

        return binding.root
    }

    private fun fetcData() {
        firebaseDatabase.reference.child("users")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val followersMap: MutableList<Map<String,String>> = mutableListOf()
                    var followersList = mutableListOf<String>()
                    var isFound = false
                    if(snapshot.exists()){
                        for(snap in snapshot.children){

                           val user: ChatUserListModel = snap.getValue(ChatUserListModel::class.java)!!
                            if(user.userId == uid){
                                if(user.followers != null){
                                    followersList = user.followers!!
                                    isFound = true
                                    break
                                }
                            }
                        }
                        if(isFound){
                                if(followersList.isNotEmpty()){
                                        for(snap in snapshot.children){
                                            val user: ChatUserListModel = snap.getValue(ChatUserListModel::class.java)!!
                                            if(user.userId in followersList){
                                                followersList.removeIf { it == user.userId }
                                                val userDataMap: MutableMap<String,String> = mutableMapOf()
                                                userDataMap["username"] = user.username?:"Unknown"
                                                userDataMap["userImage"] = user.userimage?:""
                                                userDataMap["userId"] = user.userId?:""
                                                followersMap.add(userDataMap)
                                            }
                                        }

                                        (binding.followersListRecycler.adapter as FollowersAdapter).updateList(followersMap)
                                }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun setUpAdapterWithRecycleView() {
        val recyclerView = binding.followersListRecycler
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = FollowersAdapter{id->
            startActivity(Intent(context, ChatRoom::class.java).also {
                it.putExtra(ChatRoom.UID_ARG,id)
            })
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.findViewById<View>(R.id.bottom_nav_bar_owner)?.visibility = View.VISIBLE
    }
}