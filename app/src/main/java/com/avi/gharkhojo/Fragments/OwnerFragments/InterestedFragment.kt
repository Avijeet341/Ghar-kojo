package com.avi.gharkhojo.Fragments.OwnerFragments

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.avi.gharkhojo.Adapter.InterestedUsersAdapter
import com.avi.gharkhojo.Model.InterestedUser
import com.avi.gharkhojo.R

class InterestedFragment : Fragment(R.layout.fragment_interest) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var progressBar: View
    private lateinit var emptyStateLayout: View
    private lateinit var backButton: ImageButton

    private val interestedUsersAdapter = InterestedUsersAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        progressBar = view.findViewById(R.id.progressBar)
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)
        backButton = view.findViewById(R.id.backButton)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = interestedUsersAdapter

        // Check if sample data is not empty
        val sampleUsers = getSampleInterestedUsers()
        if (sampleUsers.isEmpty()) {
            showEmptyState()
        } else {
            interestedUsersAdapter.submitList(sampleUsers)
            showRecyclerView()
        }

        swipeRefreshLayout.setOnRefreshListener {
            // Refresh data (Replace with real API call)
            swipeRefreshLayout.isRefreshing = false
        }

        backButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun getSampleInterestedUsers(): List<InterestedUser> {
        // Sample data. Replace with real API data
        return listOf(
            InterestedUser("1", "John Doe", "9876543210", "john.doe@example.com", R.drawable.vibe, "2025-01-20"),
            InterestedUser("2", "Jane Smith", "9876543211", "jane.smith@example.com", R.drawable.vibe, "2025-01-19")
        )
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
