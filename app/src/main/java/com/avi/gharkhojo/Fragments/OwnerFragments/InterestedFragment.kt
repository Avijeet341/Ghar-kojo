package com.avi.gharkhojo.Fragments.OwnerFragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.media3.common.util.Log
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.avi.gharkhojo.Adapter.InterestedUsersAdapter
import com.avi.gharkhojo.Chat.ChatRoom
import com.avi.gharkhojo.Model.ChatUserListModel
import com.avi.gharkhojo.Model.InterestedUser
import com.avi.gharkhojo.Model.Post
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.Model.UserDetails
import com.avi.gharkhojo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class InterestedFragment : Fragment(R.layout.fragment_interest) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBar: View
    private lateinit var emptyStateLayout: View
    private lateinit var backButton: ImageButton

    private var firebaseDatabase: FirebaseDatabase? = FirebaseDatabase.getInstance()
    private val interestedUsersAdapter = InterestedUsersAdapter({ user->
        startActivity(Intent(context, ChatRoom::class.java).also {
            it.putExtra(ChatRoom.UID_ARG,user.uid)
        })
    },{user->
        findNavController().navigate(R.id.action_interestedFragment_to_ownerDetailFragment
        , Bundle().also {
            it.putParcelable("post",user.post)
            })
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        progressBar = view.findViewById(R.id.progressBar)
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)
        backButton = view.findViewById(R.id.backButton)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = interestedUsersAdapter


       getData()


        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
        }

        backButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun getData(){

        firebaseDatabase!!.reference.child("BookMark")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

                            val userSnapshot = firebaseDatabase?.reference?.child("users")?.get()?.await()
                            val userMap = mutableMapOf<String, ChatUserListModel>()
                            userSnapshot?.children?.forEach { snap ->
                                val user = snap.getValue(ChatUserListModel::class.java)
                                user?.userId?.let { userMap[it] = user }
                            }

                            val newInterestUsersList = mutableListOf<InterestedUser>()

                            if (snapshot.exists()) {
                                for (userSnap in snapshot.children) {
                                    val bookmarkedUserId = userSnap.key ?: continue
                                    if (bookmarkedUserId == currentUserId) continue

                                    for (postSnap in userSnap.children) {
                                        val post = postSnap.getValue(Post::class.java) ?: continue

                                        if (post.userId == currentUserId) {
                                            val postTime = post.postTime ?: continue
                                            val user = userMap[bookmarkedUserId] ?: continue

                                            val interestedUser = InterestedUser(
                                                postTime,
                                                user.userId ?: "",
                                                user.username ?: "Unknown",
                                                user.userimage,
                                                post.post_InterestedTime?:postSnap.key.toString(),
                                                post
                                            )

                                            newInterestUsersList.add(interestedUser)
                                        }
                                    }
                                }
                            }

                            withContext(Dispatchers.Main) {

                                if (newInterestUsersList.isEmpty()) {
                                    showEmptyState()
                                } else {
                                    interestedUsersAdapter.updateList(newInterestUsersList)
                                    showRecyclerView()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("BookmarkListener", "Exception: ${e.message}", e)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("BookmarkListener", "Database error: ${error.message}")
                }
            })




    }


    private fun showRecyclerView() {
        recyclerView.visibility = View.VISIBLE
        emptyStateLayout.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    private fun showEmptyState() {
        recyclerView.visibility = View.GONE
        emptyStateLayout.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

}
