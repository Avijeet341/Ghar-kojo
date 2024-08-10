package com.avi.gharkhojo.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Adapter.GridAdapter
import com.avi.gharkhojo.Adapter.HousingTypeAdapter
import com.avi.gharkhojo.Model.GridItem
import com.avi.gharkhojo.Model.HousingType
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.OwnerActivity
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentHomeBinding
import com.bumptech.glide.RequestManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @Inject lateinit var requestManager: RequestManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserProfile()
        setupToolbar()
        setupGridView()

    }

    private fun setupUserProfile() {
        UserData.profilePictureUrl?.let { url ->
            requestManager.load(url).placeholder(R.drawable.vibe).into(binding.userImage)
        } ?: run {
            binding.userImage.setImageResource(R.drawable.vibe)
        }
        binding.username.text = UserData.username ?: getString(R.string.default_username)
    }

    private fun setupToolbar() {
        val recyclerView: RecyclerView = binding.toolbar.findViewById(R.id.housingTypeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val housingTypes = listOf(
            HousingType(R.drawable.ic_baseline_add_24, "Add Property"),
            HousingType(R.drawable.home, "House"),
            HousingType(R.drawable.apartment, "Apartment"),
            HousingType(R.drawable.building, "Flat"),
            HousingType(R.drawable.dormitory, "Dormitory"),
            HousingType(R.drawable.luxury, "Luxury"),
            HousingType(R.drawable.commercial_property, "Commercial")
        )

        val adapter = HousingTypeAdapter(housingTypes){

                val intent = Intent(requireContext(), OwnerActivity::class.java)
                startActivity(intent)
            this.requireActivity().finish()
        }
        recyclerView.adapter = adapter
    }

    private fun setupGridView() {
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = GridAdapter(createGridList()) { gridItem, _ ->
                val action = HomeDirections.actionHome2ToHomeDetails(gridItem)
                findNavController().navigate(action)
            }
        }
    }

    private fun createGridList(): ArrayList<GridItem> {
        return arrayListOf(
            GridItem(R.drawable.house, "5000", "3"),
            GridItem(R.drawable.hall, "5000", "3"),
            GridItem(R.drawable.bedroom, "5000", "3"),
            GridItem(R.drawable.entry, "5000", "3"),
            GridItem(R.drawable.base, "5000", "3"),
            GridItem(R.drawable.living, "5000", "3"),
            GridItem(R.drawable.bath, "5000", "3"),
            GridItem(R.drawable.shoe, "5000", "3"),
            GridItem(R.drawable.room_view_2, "5000", "3"),
            GridItem(R.drawable.room_view_3, "5000", "3"),
            GridItem(R.drawable.space_light, "24000", "2"),
            GridItem(R.drawable.court, "25000", "2")
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
