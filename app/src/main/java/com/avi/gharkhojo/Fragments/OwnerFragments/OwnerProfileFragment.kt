package com.avi.gharkhojo.Fragments.OwnerFragments

import android.R.attr.action
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentOwnerProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class OwnerProfileFragment : Fragment() {

    private var _binding: FragmentOwnerProfileBinding? = null
    private val binding get() = _binding!!
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOwnerProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupProfileInfo()
        setupButtons()
    }

    private fun setupProfileInfo() {
        databaseReference.child("Posts").child(firebaseUser!!.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    UserData.postCount = snapshot.childrenCount.toInt()
                    binding.postsCount.text = UserData.postCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        binding.followersCount.text = UserData.followersCount.toString()
        binding.TenetsCount.text = UserData.tenentsCount.toString()
        binding.usernameDisplay.text = "@${UserData.username}"

        // Contact information
        binding.textViewEmail.text = UserData.email
        binding.textViewUsername.text = UserData.username
        binding.textViewPhone.text = UserData.phn_no

        // Address information
        binding.textViewHouseNo.text = UserData.HouseNo
        binding.textRoadNo.text = UserData.Road_Lane
        binding.textViewColony.text = UserData.colony
        binding.textViewArea.text = UserData.Area
        binding.textViewCity.text = UserData.City
        binding.textViewState.text = UserData.State
        binding.textViewPincode.text = UserData.Pincode
    }

    private fun setupButtons() {
        binding.editProfileButton.setOnClickListener {

        }

        binding.shareProfileButton.setOnClickListener {

        }
        binding.posts.setOnClickListener {
           val action =  OwnerProfileFragmentDirections.actionOwnerProfileFragmentToUploadsFragment()
            findNavController().navigate(action)
            activity?.findViewById<ChipNavigationBar>(R.id.bottom_nav_bar_owner)?.setItemSelected(R.id.uploadsFragment, true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = OwnerProfileFragment()
    }
}