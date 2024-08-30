package com.avi.gharkhojo.Fragments.OwnerFragments

import android.app.Activity
import android.content.Intent
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.avi.gharkhojo.Adapter.PhotoAdapter
import com.avi.gharkhojo.Model.Post
import com.avi.gharkhojo.Model.PostDetails
import com.avi.gharkhojo.Model.PostDetails.clearAll
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentRoomPhotosBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.storage
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.math.abs

class RoomPhotosFragment : Fragment() {

    private var _binding: FragmentRoomPhotosBinding? = null
    private val binding get() = _binding!!

    private val roomTypes = arrayOf("CoverImage","BedRoom", "Kitchen", "WashRoom", "Toilet", "Balcony", "Hall", "Parking", "Extra")
    private val photoList = mutableListOf<String>()
    private lateinit var photoAdapter: PhotoAdapter
    private var selectedRoomType: String? = null
    private var coverImage: String = ""

    private var databaseReference: DatabaseReference? = FirebaseDatabase.getInstance().reference.child("Posts").child(
        FirebaseAuth.getInstance().currentUser!!.uid)
    private var storageReference = Firebase.storage.reference.child("Posts/${FirebaseAuth.getInstance().currentUser!!.uid}")

    companion object {
        private const val PICK_IMAGES_REQUEST = 1
        private const val PICK_SINGLE_IMAGE_REQUEST = 11
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
        setupBackButton()
        setupButtons()
    }

    private fun setupBackButton() {
       binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

    }
    private fun setupSpinner() {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, roomTypes)
        (binding.spinnerContainer.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        (binding.spinnerContainer.editText as? AutoCompleteTextView)?.setOnItemClickListener { parent, _, position, _ ->
            selectedRoomType = parent.getItemAtPosition(position) as String
            if(selectedRoomType!= "CoverImage" && PostDetails.imageList.get(selectedRoomType)?.size!! !=0){
                photoList.clear()
                photoList.addAll(PostDetails.imageList.get(selectedRoomType)!!)
                binding.selectedRoomTypeTextView.text = "${selectedRoomType} Total images: ${PostDetails.imageList.get(selectedRoomType)?.size}"
            }
            else if(selectedRoomType == "CoverImage"){
                photoList.clear()
                if(!PostDetails.coverImage.isNullOrEmpty()) {
                    photoList.add(PostDetails.coverImage!!)

                }

                binding.selectedRoomTypeTextView.text = "${selectedRoomType}"
            }
            else{
                photoList.clear()
                binding.selectedRoomTypeTextView.text = "${selectedRoomType}"
            }
            updateViewPager()

            binding.pickPhotosButton.isEnabled = true
        }
    }

    private fun setupViewPager() {
        photoAdapter = PhotoAdapter(photoList)
        binding.photoViewPager.adapter = photoAdapter


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
                var spinner = binding.spinnerContainer.editText as AutoCompleteTextView
                if(spinner.text.toString() == "CoverImage"){
                    openSingleImagePicker();
                }else {
                    openImagePicker()
                }
            } else {
                Toast.makeText(context, "Please select a room type first", Toast.LENGTH_SHORT).show()
            }
        }

        binding.deleteSelected.setOnClickListener {
            if((binding.spinnerContainer.editText as? AutoCompleteTextView)?.text.toString() == "CoverImage"){
                coverImage = ""
                photoList.clear()
                binding.selectedRoomTypeTextView.text = "${selectedRoomType}"
                PostDetails.coverImage = ""
                updateViewPager()
                return@setOnClickListener
            }
            photoList.clear()
            PostDetails.imageList.get(selectedRoomType!!)?.clear()
            binding.selectedRoomTypeTextView.text = "${selectedRoomType} Total images: ${PostDetails.imageList.get(selectedRoomType)?.size}"
            updateViewPager()
        }

        binding.uploadButton.setOnClickListener {
            if (PostDetails.isAllFieldsFilled()) {
                binding.uploadProgressBar.visibility = View.VISIBLE
                binding.pickPhotosButton.isEnabled = false
                binding.uploadButton.isEnabled = false
                binding.deleteSelected.isEnabled = false

                val post = PostDetails.saveData()
                post.userId = FirebaseAuth.getInstance().currentUser!!.uid
                post.ownerImage = UserData.profilePictureUrl

                CoroutineScope(Dispatchers.Main).launch {
                            val isUploadSuccessful = uploadPost(post)
                            if (isUploadSuccessful) {
                                clearAll()
                                photoList.clear()
                                binding.selectedRoomTypeTextView.text =
                                    "${selectedRoomType} Total images: ${PostDetails.imageList[selectedRoomType]?.size}"
                                binding.roomTypeSpinner.setSelection(0)
                                updateViewPager()
                                Toast.makeText(
                                    context,
                                    "Post Uploaded Successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                this@RoomPhotosFragment.findNavController()
                                    .navigate(R.id.uploadsFragment)
                                (activity as AppCompatActivity).findViewById<ChipNavigationBar>(R.id.bottom_nav_bar_owner).visibility = View.VISIBLE
                                (activity as? AppCompatActivity)?.findViewById<ChipNavigationBar>(R.id.bottom_nav_bar_owner)?.setItemSelected(R.id.uploadsFragment)

                            } else {
                                Toast.makeText(
                                    context,
                                    "Post Upload Failed!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            binding.uploadProgressBar.visibility = View.GONE
                            binding.pickPhotosButton.isEnabled = true
                            binding.uploadButton.isEnabled = true
                            binding.deleteSelected.isEnabled = true
                }
            }
            else{
                if(PostDetails.coverImage.isNullOrEmpty()){
                    Toast.makeText(context, "Please select a cover image", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openSingleImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_SINGLE_IMAGE_REQUEST)
    }

    private suspend fun uploadPost(post: Post): Boolean = withContext(Dispatchers.IO) {
        try {
            post.postTime = System.currentTimeMillis().toString()
            val uploadTasks = post.imageList.flatMap { (key, images) ->
                images.map { imageUri ->
                    async {
                        storageReference.child(post.postTime!!).child(key).child(System.currentTimeMillis().toString()).putFile(Uri.parse(imageUri)).await()
                    }
                }
            }
            async {
                storageReference.child(post.postTime.toString()).child("coverImage").putFile(Uri.parse(post.coverImage)).await()
            }.await()

            uploadTasks.awaitAll()

            val downloadUrlTasks = post.imageList.map { (key, _) ->

                async {
                    if(post.imageList[key]?.isEmpty() != true){
                        val downloadUrls = storageReference.child(post.postTime.toString()).child(key).listAll().await().items.map { item ->
                            item.downloadUrl.await().toString()
                        }
                        post.imageList[key]?.clear()
                        post.imageList[key]?.addAll(downloadUrls)
                    }

                }
            }
            storageReference.child(post.postTime.toString()).child("coverImage").downloadUrl.await()?.let {
                post.coverImage = it.toString()
            }

            downloadUrlTasks.awaitAll()

            databaseReference?.push()?.setValue(post)?.await()

            return@withContext true
        } catch (e: Exception) {
            return@withContext false
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
                if (intent.clipData != null) {
                    val count = intent.clipData!!.itemCount
                    for (i in 0 until count) {
                        val imageUri = intent.clipData!!.getItemAt(i).uri
                        photoList.add(imageUri.toString())
                        PostDetails.imageList.get(selectedRoomType!!)?.add(imageUri.toString())
                    }

                    binding.selectedRoomTypeTextView.text = "${selectedRoomType} Total images: ${PostDetails.imageList.get(selectedRoomType)?.size}"
                } else {
                    intent.data?.let { uri ->
                        coverImage = uri.toString()
                        photoList.add(uri.toString())
                        PostDetails.imageList.get(selectedRoomType!!)?.add(uri.toString())
                        binding.selectedRoomTypeTextView.text = "${selectedRoomType} Total images: ${PostDetails.imageList.get(selectedRoomType)?.size}"
                    }
                }
                updateViewPager()
            }
        }
        else if(requestCode == PICK_SINGLE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK){
            data?.let { intent ->
                if(intent.data != null){
                    intent.data?.let {
                        uri->
                        photoList.clear()
                        PostDetails.coverImage = ""
                        coverImage = uri.toString()
                        photoList.add(uri.toString())
                        PostDetails.coverImage = uri.toString()
                        updateViewPager()
                    }
                }

            }
        }
    }

    private fun updateViewPager() {
        if (photoList.isNotEmpty()) {
            binding.selectedRoomTypeContainer.visibility = View.VISIBLE
            photoAdapter.updatePhotos(photoList)
        } else {
           binding.selectedRoomTypeContainer.visibility = View.GONE
        }
        if(PostDetails.imageList.any{it.value.isNotEmpty()}){
            binding.uploadButton.isEnabled = true
        }
        else{
            binding.uploadButton.isEnabled = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}