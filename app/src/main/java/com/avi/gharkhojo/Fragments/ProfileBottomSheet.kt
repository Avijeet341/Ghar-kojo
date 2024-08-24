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
import com.avi.gharkhojo.Model.UserDetails
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


class ProfileBottomSheet() : BottomSheetDialogFragment() {

    var profileBinding: FragmentProfileBinding? = null
    private var _binding: FragmentProfileBottomSheetBinding? = null
    private val binding get() = _binding!!
    val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
      var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
      var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("users")

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
        showDetails();
        return binding.root
    }

    private fun showDetails() {
        binding.editTextName.setText(UserData.username)
        binding.editTextPhone.setText(UserData.phn_no)
        binding.editTextPincode.setText(UserData.Pincode)
        binding.editTextHouseNo.setText(UserData.HouseNo)
        binding.editTextCity.setText(UserData.City)
        binding.editTextState.setText(UserData.State)
        binding.editTextArea.setText(UserData.Area)
        binding.editTextLandmark.setText(UserData.LandMark)
        binding.editTextColony.setText(UserData.colony)
        binding.editTextRoadNo.setText(UserData.Road_Lane)
        binding.buttonSave.isClickable = true

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        isCancelable = false

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.buttonSave.setOnClickListener {
            Log.d("ProfileBottomSheet", "Save button clicked")

            saveProfileData()
        }
    }

    private fun saveProfileData() {


        if(isAllFieldFilled())
        {
            binding.buttonSave.isClickable = false
            binding.buttonSave.visibility = View.GONE
            binding.progressCircularLogin.visibility = View.VISIBLE
            var userDetails: UserDetails = UserDetails()
            userDetails.username = binding.editTextName.text.toString().trim()
            userDetails.phn_no = binding.editTextPhone.text.toString().trim()
            userDetails.Pincode = binding.editTextPincode.text.toString().trim()
            userDetails.HouseNo = binding.editTextHouseNo.text.toString().trim()
            userDetails.City = binding.editTextCity.text.toString().trim()
            userDetails.State = binding.editTextState.text.toString().trim()
            userDetails.Area = binding.editTextArea.text.toString().trim()
            userDetails.LandMark = binding.editTextLandmark.text.toString().trim()
            userDetails.colony = binding.editTextColony.text.toString().trim()
            userDetails.Road_Lane = binding.editTextRoadNo.text.toString().trim()

            val user = firebaseAuth.currentUser
            if (user != null) {

                UserData.username = userDetails.username
                UserData.phn_no = userDetails.phn_no
                UserData.Pincode = userDetails.Pincode
                UserData.HouseNo = userDetails.HouseNo
                UserData.City = userDetails.City
                UserData.State = userDetails.State
                UserData.Area = userDetails.Area
                UserData.LandMark = userDetails.LandMark
                UserData.colony = userDetails.colony
                UserData.Road_Lane = userDetails.Road_Lane


                if (profileBinding != null) {
                  profileBinding?.textViewUsername?.setText(userDetails.username)
                    profileBinding?.textViewPhone?.setText(userDetails.phn_no)
                    profileBinding?.textViewPincode?.setText(userDetails.Pincode)
                    profileBinding?.textViewHouseNo?.setText(userDetails.HouseNo)
                    profileBinding?.textViewCity?.setText(userDetails.City)
                    profileBinding?.textViewState?.setText(userDetails.State)
                    profileBinding?.textViewArea?.setText(userDetails.Area)
                    profileBinding?.textViewLandmark?.setText(userDetails.LandMark)
                    profileBinding?.textViewColony?.setText(userDetails.colony)
                    profileBinding?.textRoadNo?.setText(userDetails.Road_Lane)
                }

                firestore.collection("users").document(user.uid).set(userDetails)
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
        else{
            Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show()
            binding.buttonSave.visibility = View.VISIBLE
            binding.progressCircularLogin.visibility = View.GONE
        }

    }

    private fun isAllFieldFilled(): Boolean {

        if (binding.editTextName.text.toString().trim().isEmpty()) {
            binding.editTextName.error = "Name is required"
            return false
        }
        if (binding.editTextPhone.text.toString().trim().isEmpty()) {
            binding.editTextPhone.error = "Phone is required"
            return false
        }
        if (binding.editTextPincode.text.toString().trim().isEmpty()) {
            binding.editTextPincode.error = "Pincode is required"
            return false
        }
        if (binding.editTextHouseNo.text.toString().trim().isEmpty()) {
            binding.editTextHouseNo.error = "House No is required"
            return false
        }
        if (binding.editTextCity.text.toString().trim().isEmpty()) {
            binding.editTextCity.error = "City is required"
            return false
        }
        if (binding.editTextState.text.toString().trim().isEmpty()) {
            binding.editTextState.error = "State is required"
            return false
        }
        if (binding.editTextArea.text.toString().trim().isEmpty()) {
            binding.editTextArea.error = "Area is required"
            return false
        }
        if (binding.editTextLandmark.text.toString().trim().isEmpty()) {
            binding.editTextLandmark.error = "Landmark is required"
            return false
        }
        if (binding.editTextColony.text.toString().trim().isEmpty()) {
            binding.editTextColony.error = "Colony is required"
            return false
        }
        if (binding.editTextRoadNo.text.toString().trim().isEmpty()) {
            binding.editTextRoadNo.error = "Road No is required"
            return false
        }
        return true


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
