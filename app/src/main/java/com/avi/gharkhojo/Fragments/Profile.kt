package com.avi.gharkhojo.Fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.avi.gharkhojo.LoginActivity
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.yalantis.ucrop.UCrop
import java.io.File
import java.util.*

class Profile : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private lateinit var pickImage: ActivityResultLauncher<String>
    private lateinit var cropImage: ActivityResultLauncher<Intent>
    private val storageRef: StorageReference by lazy { Firebase.storage.reference.child("profile_pictures/${FirebaseAuth.getInstance().currentUser?.uid}") }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfileImage()
        setupClickListeners()
        initImagePicker()
    }

    private fun loadProfileImage() {
        val photoUrl = UserData.profilePictureUrl
        Glide.with(this)
            .load(photoUrl ?: R.drawable.india)
            .placeholder(R.drawable.india)
            .error(R.drawable.background2)
            .centerCrop()
            .into(binding.ProfilePic)
    }

    private fun setupClickListeners() {
        binding.ProfilePic.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.buttonSignOut.setOnClickListener {
            signOut()
        }

        binding.fabEditProfile.setOnClickListener {
            showProfileEditBottomSheet()
        }
    }

    private fun loadUserData() {
        binding.textViewUsername.text = UserData.username ?: getString(R.string.default_username)
        binding.textViewEmail.text = UserData.email ?: getString(R.string.default_email)
        binding.textViewAddress.text = UserData.address ?: getString(R.string.default_address)
        binding.textViewPhone.text = UserData.phn_no ?: getString(R.string.default_phone)
    }

    private fun initImagePicker() {
        pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { startCrop(it) }
        }

        cropImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    val resultUri = UCrop.getOutput(it)
                    resultUri?.let { uri ->
                        UserData.profilePictureUrl = uri.toString()
                        storageRef.putFile(uri).addOnSuccessListener {
                            storageRef.downloadUrl.addOnSuccessListener {
                                UserData.profilePictureUrl = it.toString()
                            }
                        }
                        Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.india)
                            .error(R.drawable.background2)
                            .centerCrop()
                            .into(binding.ProfilePic)
                    }
                }
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val cropError = UCrop.getError(result.data!!)
                Toast.makeText(requireContext(), cropError?.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "croppedImage_${UUID.randomUUID()}.jpg"))
        val options = UCrop.Options().apply {
            setFreeStyleCropEnabled(true)
            setHideBottomControls(true)
            setShowCropGrid(false)
            setCropFrameColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
        val uCrop = UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withOptions(options)

        cropImage.launch(uCrop.getIntent(requireContext()))
    }

    private fun signOut() {
        firebaseAuth.signOut()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInClient.signOut().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Clear UserData
                UserData.clear()

                Toast.makeText(requireContext(), getString(R.string.sign_out_success), Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                requireActivity().finishAffinity()
            } else {
                Toast.makeText(requireContext(), getString(R.string.sign_out_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProfileEditBottomSheet() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            val address = binding.textViewAddress.text.toString()
            val phone = binding.textViewPhone.text.toString()

            val profileBottomSheet = ProfileBottomSheet(
                UserData.username ?: getString(R.string.default_username),
                UserData.address ?: getString(R.string.default_address),
                UserData.phn_no ?: getString(R.string.default_phone)
            )
            profileBottomSheet.profileBinding = binding
            profileBottomSheet.show(childFragmentManager, ProfileBottomSheet::class.java.simpleName)
        }
    }

    override fun onStart() {
        super.onStart()
        loadUserData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
