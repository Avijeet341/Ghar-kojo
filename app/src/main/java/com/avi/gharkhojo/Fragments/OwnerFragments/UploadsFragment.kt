package com.avi.gharkhojo.Fragments.OwnerFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.avi.gharkhojo.Adapter.UploadsAdapter
import com.avi.gharkhojo.Model.Post
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentUploadsBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UploadsFragment : Fragment() {

    private lateinit var binding: FragmentUploadsBinding
    private lateinit var uploadsAdapter: UploadsAdapter
    private var databaseReference: DatabaseReference? = FirebaseDatabase.getInstance().reference.child("Posts")
    private var storageReference = Firebase.storage.reference.child("Posts/${FirebaseAuth.getInstance().currentUser!!.uid}")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUploadsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

           val otherId: String? =  arguments?.getString("uid")?: FirebaseAuth.getInstance().uid
        val bottomNav = activity?.findViewById<ChipNavigationBar>(R.id.bottom_nav_bar)
        if((!otherId.isNullOrEmpty()) && otherId != FirebaseAuth.getInstance().uid){
            bottomNav?.visibility = View.GONE
        }
            loadData(otherId.toString())

    }

    private fun loadData(uid:String){
        uploadsAdapter = UploadsAdapter( { post ->

            val navController = findNavController()
            var bundle = Bundle()
            bundle.putParcelable("post",post)
            if (post.userId == FirebaseAuth.getInstance().uid) {
                navController.navigate(R.id.action_uploadsFragment_to_ownerDetailFragment,bundle)
            } else {
                navController.navigate(R.id.UploadsFragmentToOwnerDetailFragment,bundle)
            }




        }) { post, dialog ->

            CoroutineScope(Dispatchers.Main).launch {
                dialog?.dismiss()

                binding.postLoading.visibility = View.VISIBLE
                deletePost(post)
                binding.postLoading.visibility = View.GONE


            }
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = uploadsAdapter
        }
        CoroutineScope(Dispatchers.Main).launch {
            binding.postLoading.visibility = View.VISIBLE
            fetchAndUploadData(uid)
        }
    }
    suspend fun deletePost(post: Post) = withContext(Dispatchers.IO) {
        try {
            for ((key, _) in post.imageList) {
                val folderRef = storageReference.child(post.postTime!!).child(key)
                val listResult = folderRef.listAll().await()

                for (item in listResult.items) {
                    item.delete().await()
                }
            }

            storageReference.child(post.postTime!!).child("coverImage").delete().await()

            val userPostsSnapshot = databaseReference?.child(post.userId.toString())?.get()?.await()
            userPostsSnapshot?.children?.forEach { snapshot ->
                val tempPost = snapshot.getValue(Post::class.java)
                if (tempPost == post) {
                    databaseReference?.child(post.userId.toString())?.child(snapshot.key!!)?.removeValue()?.await()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT).show()
                        loadData(post.userId.toString())
                    }
                    return@withContext
                }
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Post Deletion Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    suspend fun fetchAndUploadData(uid: String) = withContext(Dispatchers.IO) {
        try {
            val tempList = mutableListOf<Post>()
           databaseReference?.child(uid)?.addValueEventListener(object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {
                   binding.postLoading.visibility = View.VISIBLE
                   tempList.clear()
                   uploadsAdapter.updateData(tempList)

                   if (snapshot != null && snapshot.exists()) {

                       binding.noUploadLayout.visibility = View.GONE
                       for (dataSnapshot in snapshot.children) {
                           val post = dataSnapshot.getValue(Post::class.java)
                           if (post != null) {
                               tempList.add(post)
                           }
                       }

                           binding.postLoading.visibility = View.GONE
                           uploadsAdapter.updateData(tempList)
                           Log.d("size", "Fetched upload list size: ${tempList.size}")
                   } else {
                       binding.postLoading.visibility = View.GONE
                       binding.noUploadLayout.visibility = View.VISIBLE
                       Log.d("fetchAndUploadData", "Snapshot is null or empty")
                   }
               }

               override fun onCancelled(error: DatabaseError) {
                 binding.postLoading.visibility = View.GONE
                   Toast.makeText(context, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
               }

           })

        } catch (e: Exception) {
            binding.postLoading.visibility = View.GONE
            Log.e("fetchAndUploadData", "Error fetching data", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}
