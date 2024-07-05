package com.avi.gharkhojo.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.avi.gharkhojo.Adapter.GridAdapter
import com.avi.gharkhojo.Model.GridItem
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentHomeBinding

class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupStaggeredGridView()
        binding.textViewHeader.apply {

        }
    }

    private fun setupStaggeredGridView() {
        binding.recyclerView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = GridAdapter(createGridList()) { gridItem, position ->
                Toast.makeText(requireContext(), "Clicked on GridItem: ${gridItem.rent}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createGridList(): ArrayList<GridItem> {
        return arrayListOf(
            GridItem(R.drawable.spider3, "10000", "2"),
            GridItem(R.drawable.hashira, "12000", "3"),
            GridItem(R.drawable.india, "9000", "2"),
            GridItem(R.drawable.kk, "11000", "4"),
            GridItem(R.drawable.pain, "13000", "5"),
            GridItem(R.drawable.giyu_tomioka, "14000", "2"),
            GridItem(R.drawable.doodle, "15000", "2"),
            GridItem(R.drawable.itachi, "16000", "3"),
            GridItem(R.drawable.family, "17000", "2"),
            GridItem(R.drawable.kimetsu_no_yaiba, "18000", "2"),
            GridItem(R.drawable.kokushibo, "19000", "2"),
            GridItem(R.drawable.madara, "20000", "2"),
            GridItem(R.drawable.vibe, "21000", "2"),
            GridItem(R.drawable.this_is_good, "22000", "4"),
            GridItem(R.drawable.squad, "23000", "2"),
            GridItem(R.drawable.space_light, "24000", "2"),
            GridItem(R.drawable.court, "25000", "2")
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
