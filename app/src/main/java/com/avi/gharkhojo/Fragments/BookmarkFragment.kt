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
import com.avi.gharkhojo.Model.Post
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
            ),
            House(
                imageUrl = R.drawable.home3,
                title = "Modern Loft",
                location = "New York, USA",
                priceAmount = "$3,200",
                bedrooms = "4",
                bathrooms = "3",
                area = "1500 sq.ft."
            ),
            House(
                imageUrl = R.drawable.home4,
                title = "Charming Cottage",
                location = "Lake Tahoe, USA",
                priceAmount = "$2,800",
                bedrooms = "3",
                bathrooms = "2",
                area = "1200 sq.ft."
            )
         ,House(
                imageUrl = R.drawable.home5,
                title = "Elegant Mansion",
                location = "Paris, France",
                priceAmount = "$8,000",
                bedrooms = "7",
                bathrooms = "6",
                area = "3500 sq.ft."
            )
,
            House(
                imageUrl = R.drawable.home6,
                title = "Cozy Cabin",
                location = "Aspen, USA",
                priceAmount = "$1,800",
                bedrooms = "2",
                bathrooms = "1",
                area = "900 sq.ft."
            )


        )


        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = BookmarkAdapter(houses) { house ->

            val action = BookmarkFragmentDirections.actionBookmarkFragmentToHomeDetails()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
