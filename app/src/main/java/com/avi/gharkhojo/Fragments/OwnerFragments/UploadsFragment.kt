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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UploadsFragment : Fragment() {

    private lateinit var binding: FragmentUploadsBinding
    private lateinit var uploadsAdapter: UploadsAdapter
    private var databaseReference: DatabaseReference? = FirebaseDatabase.getInstance().reference.child("Posts")

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
            binding.postLoading.visibility = View.VISIBLE
            CoroutineScope(Dispatchers.IO).launch {
                dialog?.dismiss()

                databaseReference?.child(uid)?.get()?.addOnCompleteListener {
                    if(it.isSuccessful){
                        for(dataSnapshot in it.result.children){
                            val tempPost = dataSnapshot.getValue(Post::class.java)
                            if(tempPost?.equals(post) == true){
                                databaseReference?.child(uid)?.child(dataSnapshot.key.toString())?.removeValue()?.addOnCompleteListener {
                                    if(it.isSuccessful){
                                        Toast.makeText(context, "Post Deleted Successfully", Toast.LENGTH_SHORT).show()
                                        binding.postLoading.visibility = View.GONE
                                        loadData(uid)
                                    }
                                    else{
                                        Toast.makeText(context, "Post Deletion Failed", Toast.LENGTH_SHORT).show()
                                        binding.postLoading.visibility = View.GONE
                                    }
                                    }
                                break
                            }
                        }
                    }
                }

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

    suspend fun fetchAndUploadData(uid: String) = withContext(Dispatchers.IO) {
        try {
           databaseReference?.child(uid)?.addValueEventListener(object : ValueEventListener {
               override fun onDataChange(snapshot: DataSnapshot) {
                   if (snapshot != null && snapshot.exists()) {
                       val tempList = mutableListOf<Post>()
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
