package com.avi.gharkhojo.Fragments.OwnerFragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.widget.ImageButton
import com.avi.gharkhojo.Adapter.PhotoAdapter
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentRoomPhotosBinding
import kotlin.math.abs

class RoomPhotosFragment : Fragment() {

    private var _binding: FragmentRoomPhotosBinding? = null
    private val binding get() = _binding!!

    private val roomTypes = arrayOf("BedRoom", "Kitchen", "WashRoom", "Toilet", "Balcony", "Hall", "Parking", "Extra")
    private val photoList = mutableListOf<Uri>()
    private lateinit var photoAdapter: PhotoAdapter
    private var selectedRoomType: String? = null

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
        setupBackButton(view)
        setupButtons()
    }

    private fun setupBackButton(view: View) {
        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            // Navigate back to the parent fragment
            parentFragmentManager.popBackStack()
        }
    }
    private fun setupSpinner() {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, roomTypes)
        (binding.spinnerContainer.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        (binding.spinnerContainer.editText as? AutoCompleteTextView)?.setOnItemClickListener { parent, _, position, _ ->
            selectedRoomType = parent.getItemAtPosition(position) as String
            binding.selectedRoomTypeTextView.text = selectedRoomType
            binding.pickPhotosButton.isEnabled = true
        }
    }

    private fun setupViewPager() {
        photoAdapter = PhotoAdapter(photoList)
        binding.photoViewPager.adapter = photoAdapter
        binding.photoViewPager.visibility = View.GONE


        binding.photoViewPager.setPageTransformer(getTransformation())
        binding.photoViewPager.offscreenPageLimit = 3
    }

    private fun getTransformation(): CompositePageTransformer {
        return CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(30))
            addTransformer { page, position ->
                val alpha = 0.5f + 0.5f * (1 - abs(position))
                page.alpha = alpha
                page.scaleY = 0.85f + alpha * 0.15f

                val elevation = if (position == 0f) 5f else 0f
                page.translationZ = elevation

                val rotation = -20 * position
                page.rotation = rotation

                val depth = -120 * abs(position)
                page.cameraDistance = 8000f
                page.translationX = depth

                val fadeOut = if (position == 0f) 0f else 0.7f
                val fadeAlpha = 1 - fadeOut * abs(position)
                page.alpha = fadeAlpha

                val absPosition = abs(position)
                val bounceScale = if (absPosition > 1) 0.85f else (0.85f + (1 - absPosition) * 0.15f)
                page.scaleX = bounceScale
                page.scaleY = bounceScale

                if (page is ImageView) {
                    val saturation = 1 - 0.5f * abs(position)
                    val colorMatrix = ColorMatrix().apply { setSaturation(saturation) }
                    val filter = ColorMatrixColorFilter(colorMatrix)
                    page.colorFilter = filter
                }
            }
        }
    }

    private fun setupButtons() {
        binding.pickPhotosButton.isEnabled = false
        binding.pickPhotosButton.setOnClickListener {
            if (selectedRoomType != null) {
                openImagePicker()
            } else {
                Toast.makeText(context, "Please select a room type first", Toast.LENGTH_SHORT).show()
            }
        }

        binding.uploadButton.setOnClickListener {

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