package com.avi.gharkhojo.Fragments.OwnerFragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.avi.gharkhojo.Adapter.PhotoAdapter
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentRoomPhotosBinding

class RoomPhotosFragment : Fragment() {

    private var _binding: FragmentRoomPhotosBinding? = null
    private val binding get() = _binding!!

    private val roomTypes = arrayOf("BedRoom", "Kitchen", "WashRoom", "Toilet", "Balcony", "Hall", "Parking", "Extra")
    private val photoList = mutableListOf<Uri>()
    private lateinit var photoAdapter: PhotoAdapter

    companion object {
        private const val PICK_IMAGES_REQUEST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomPhotosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinner()
        setupViewPager()
        setupButtons()
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, roomTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.roomTypeSpinner.adapter = adapter

        binding.roomTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedType = roomTypes[position]
                binding.selectedRoomTypeTextView.text = "Selected: $selectedType"
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                binding.selectedRoomTypeTextView.text = ""
            }
        }
    }

    private fun setupViewPager() {
        photoAdapter = PhotoAdapter(photoList)
        binding.photoViewPager.adapter = photoAdapter
        binding.photoViewPager.visibility = View.GONE
    }

    private fun setupButtons() {
        binding.pickPhotosButton.setOnClickListener {
            openImagePicker()
        }

        binding.uploadButton.setOnClickListener {
            // TODO: Implement upload logic
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGES_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.let { intent ->
                photoList.clear()
                if (intent.clipData != null) {
                    val count = intent.clipData!!.itemCount
                    for (i in 0 until count) {
                        val imageUri = intent.clipData!!.getItemAt(i).uri
                        photoList.add(imageUri)
                    }
                } else {
                    intent.data?.let { uri ->
                        photoList.add(uri)
                    }
                }
                updateViewPager()
            }
        }
    }

    private fun updateViewPager() {
        if (photoList.isNotEmpty()) {
            binding.photoViewPager.visibility = View.VISIBLE
            photoAdapter.updatePhotos(photoList)
        } else {
            binding.photoViewPager.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}