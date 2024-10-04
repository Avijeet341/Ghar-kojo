package com.avi.gharkhojo.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.avi.gharkhojo.Adapter.BookmarkAdapter
import com.avi.gharkhojo.Model.GridItem
import com.avi.gharkhojo.Model.Post
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentBookmarkBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!
    private val databaseRef: DatabaseReference by lazy{FirebaseDatabase.getInstance().reference.child("BookMark")}
    private val houses = ArrayList<Post>()
    private lateinit var postLoading:ProgressBar
    private lateinit var adapter: BookmarkAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        postLoading=binding.postLoading
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        fetchBookmarks()
    }

    private fun setupRecyclerView() {
        adapter = BookmarkAdapter { post ->
            val action = BookmarkFragmentDirections.actionBookmarkFragmentToHomeDetails()
            val bundle = Bundle()
            bundle.putParcelable("post", post)
            action.arguments.putAll(bundle)
            findNavController().navigate(action)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    private fun fetchBookmarks() {
        postLoading.visibility = View.VISIBLE
        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                houses.clear()
                for (postSnapshot in snapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    post?.let { houses.add(it) }

                }
                adapter.updateData(houses)
                postLoading.visibility = View.GONE



            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error fetching data: ${error.message}", Toast.LENGTH_SHORT).show()
                binding.postLoading.visibility = View.GONE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
