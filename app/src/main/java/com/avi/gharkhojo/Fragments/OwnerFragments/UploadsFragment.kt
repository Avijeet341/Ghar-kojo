package com.avi.gharkhojo.Fragments.OwnerFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.avi.gharkhojo.Adapter.UploadsAdapter
import com.avi.gharkhojo.Model.Upload
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentUploadsBinding

class UploadsFragment : Fragment() {

    private lateinit var binding: FragmentUploadsBinding
    private lateinit var uploadsAdapter: UploadsAdapter
    private val uploadList = listOf(

        Upload(R.drawable.modern_house, "Modern Wood House", "Canberra, Australia", "$3,000", "/Month", 5, 4, "1500 sq.ft."),
        Upload(R.drawable.modern_house, "Modern Wood House", "Canberra, Australia", "$3,000", "/Month", 5, 4, "1500 sq.ft."),
        Upload(R.drawable.modern_house, "Modern Wood House", "Canberra, Australia", "$3,000", "/Month", 5, 4, "1500 sq.ft."),
        Upload(R.drawable.modern_house, "Modern Wood House", "Canberra, Australia", "$3,000", "/Month", 5, 4, "1500 sq.ft."),
        Upload(R.drawable.modern_house, "Modern Wood House", "Canberra, Australia", "$3,000", "/Month", 5, 4, "1500 sq.ft."),

    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUploadsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        uploadsAdapter = UploadsAdapter(uploadList) { upload ->
            val action = UploadsFragmentDirections.actionUploadsFragmentToOwnerDetailFragment()
            findNavController().navigate(action)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = uploadsAdapter
        }
    }
}
