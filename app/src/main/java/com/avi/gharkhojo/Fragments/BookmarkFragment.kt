package com.avi.gharkhojo.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.avi.gharkhojo.Adapter.BookmarkAdapter
import com.avi.gharkhojo.Model.GridItem
import com.avi.gharkhojo.Model.House
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentBookmarkBinding

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        val houses = listOf(
            House(
                imageUrl = R.drawable.home1,
                title = "Modern Wood House",
                location = "Canberra, Australia",
                priceAmount = "$3,000",
                bedrooms = "5",
                bathrooms = "4",
                area = "1500 sq.ft."
            ),
            House(
                imageUrl = R.drawable.home2,
                title = "Stylish Villa",
                location = "Sydney, Australia",
                priceAmount = "$4,500",
                bedrooms = "6",
                bathrooms = "5",
                area = "2000 sq.ft."
            )
        )


        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = BookmarkAdapter(houses) { house ->
            /* Navigate to HomeDetailsFragment without any GridItem
            * hum log home se bhi homeDetails me aa rahe hain or bookmark se bhi
            * but mr gridItem sirf home frament ke liye kar raha hun issliye gridItem
            *  ka default value send kar raha hun home deatails me  bookmark fragment se
            * */
            val action = BookmarkFragmentDirections.actionBookmarkFragmentToHomeDetails(
                selectedGridItem = GridItem(0, "", "") //  default GridItem
            )
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
