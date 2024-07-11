package com.avi.gharkhojo.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.databinding.FragmentProfileBinding
import com.avi.gharkhojo.databinding.FragmentProfileBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileBottomSheet(private val name: String, private val address: String, private val phone: String) : BottomSheetDialogFragment() {

    var profileBinding: FragmentProfileBinding? = null
    private var _binding: FragmentProfileBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBottomSheetBinding.inflate(inflater, container, false)
        binding.buttonSave.isClickable = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false
        binding.editTextName.setText(name.trim())
        binding.editTextAddress.setText(address.trim())
        binding.editTextPhone.setText(phone.trim())

        binding.buttonSave.setOnClickListener {
            Log.d("ProfileBottomSheet", "Save button clicked")
            binding.buttonSave.isClickable = false
            saveProfileData()
        }
    }

    private fun saveProfileData() {
        val newName = binding.editTextName.text.toString().trim()
        val newAddress = binding.editTextAddress.text.toString().trim()
        val newPhone = binding.editTextPhone.text.toString().trim()

        Log.d("ProfileBottomSheet", "New data - Name: $newName, Address: $newAddress, Phone: $newPhone")

        val user = firebaseAuth.currentUser
        if (user != null) {
            val userData = mapOf(
                "name" to newName,
                "address" to newAddress,
                "phone" to newPhone
            )

            UserData.username = newName
            UserData.phn_no = newPhone
            UserData.address = newAddress

            if(profileBinding!=null){
                profileBinding?.textViewUsername?.text = newName
                profileBinding?.textViewAddress?.text = newAddress
                profileBinding?.textViewPhone?.text = newPhone
            }
            firestore.collection("users").document(user.uid).set(userData)

                .addOnSuccessListener {
                    Log.d("ProfileBottomSheet", "Profile updated successfully")
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    dismissNow()
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileBottomSheet", "Failed to update profile: ${e.message}")
                    Toast.makeText(requireContext(), "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                }

        } else {
            Log.e("ProfileBottomSheet", "User not authenticated")
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
