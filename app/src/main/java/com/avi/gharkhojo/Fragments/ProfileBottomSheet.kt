package com.avi.gharkhojo.Fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.avi.gharkhojo.Model.ChatUserListModel
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentProfileBinding
import com.avi.gharkhojo.databinding.FragmentProfileBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


class ProfileBottomSheet(
    private val name: String,
    private val address: String,
    private val phone: String
) : BottomSheetDialogFragment() {

    var profileBinding: FragmentProfileBinding? = null
    private var _binding: FragmentProfileBottomSheetBinding? = null
    private val binding get() = _binding!!
     val firebaseAuth: FirebaseAuth by lazy{ FirebaseAuth.getInstance() }
     var firestore: FirebaseFirestore= FirebaseFirestore.getInstance()
     var databaseReference: DatabaseReference= FirebaseDatabase.getInstance().getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
        dialog?.window?.setWindowAnimations(R.style.DialogAnimation) // Set animation
    }

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

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        isCancelable = false
        binding.editTextName.setText(name.trim())
       // binding.editTextAddress.setText(address.trim())
        binding.editTextPhone.setText(phone.trim())

        binding.cancelButton.setOnClickListener {
            dismiss()  // Dismiss the bottom sheet when cancel button is clicked
        }

        binding.buttonSave.setOnClickListener {
            Log.d("ProfileBottomSheet", "Save button clicked")
            binding.buttonSave.isClickable = false
            binding.buttonSave.visibility = View.GONE
            binding.progressCircularLogin.visibility = View.VISIBLE
            saveProfileData()
        }
    }

    private fun saveProfileData() {
        val newName = binding.editTextName.text.toString().trim()
       /* val newAddress = binding.editTextAddress.text.toString().trim()
       🫡
        */
        val newPhone = binding.editTextPhone.text.toString().trim()

      /*  Log.d("ProfileBottomSheet", "New data - Name: $newName, Address: $newAddress, Phone: $newPhone")
      *
      * 🫡
      * */

        val user = firebaseAuth.currentUser
        if (user != null) {
            val userData = mapOf(
                "name" to newName,
//                "address" to newAddress,
                "phone" to newPhone
            )

            UserData.username = newName
            UserData.phn_no = newPhone
//            UserData.address = newAddress

            if (profileBinding != null) {
                profileBinding?.textViewUsername?.text = newName
//                profileBinding?.textViewAddress?.text = newAddress
                profileBinding?.textViewPhone?.text = newPhone
            }

            firestore.collection("users").document(user.uid).set(userData)
                .addOnSuccessListener {
                    Log.d("ProfileBottomSheet", "Profile updated successfully")
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    binding.progressCircularLogin.visibility = View.GONE
                    dismissNow()
                }
                .addOnFailureListener { e ->
                    Log.e("ProfileBottomSheet", "Failed to update profile: ${e.message}")
                    Toast.makeText(requireContext(), "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                    binding.progressCircularLogin.visibility = View.GONE
                    binding.buttonSave.visibility = View.VISIBLE
                    binding.buttonSave.isClickable = true
                }
        } else {
            Log.e("ProfileBottomSheet", "User not authenticated")
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show()
            binding.progressCircularLogin.visibility = View.GONE
            binding.buttonSave.visibility = View.VISIBLE
            binding.buttonSave.isClickable = true
        }
        databaseReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(dataSnapshot in snapshot.children){
                    val userData = dataSnapshot.getValue(ChatUserListModel::class.java)
                    if(userData?.userId.equals(FirebaseAuth.getInstance().currentUser?.uid)){
                        databaseReference.child(dataSnapshot.key.toString()).child("username").setValue(UserData.username)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
