package com.avi.gharkhojo.Fragments.OwnerFragments

import android.R.attr.action
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.navigation.fragment.findNavController
import com.avi.gharkhojo.Fragments.HomeDirections
import com.avi.gharkhojo.Fragments.Profile
import com.avi.gharkhojo.Model.ChatUserListModel
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.Model.UserDetails
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentOwnerProfileBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class OwnerProfileFragment : Fragment() {

    private var _binding: FragmentOwnerProfileBinding? = null
    private val binding get() = _binding!!
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    var UserCollection:CollectionReference = FirebaseFirestore.getInstance().collection("users")
    var otherId: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOwnerProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        otherId = arguments?.getString("uid")
        val bottomNav = activity?.findViewById<ChipNavigationBar>(R.id.bottom_nav_bar)
        if((!otherId.isNullOrEmpty()) && otherId != firebaseUser?.uid){
            loadOtherUserProfile(otherId!!)
            binding.editProfileButton.visibility = View.GONE
            bottomNav?.visibility = View.GONE
        }else{
            binding.editProfileButton.visibility = View.VISIBLE
            bottomNav?.visibility = View.VISIBLE
            bottomNav?.setItemSelected(R.id.nav_profile, true)
            setupProfileInfo(firebaseUser!!.uid)
        }

        setupButtons()
    }
    private fun loadOtherUserProfile(otherId: String) {

        databaseReference.child("users").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(dataSnapshot in snapshot.children){
                    val userData = dataSnapshot.getValue(ChatUserListModel::class.java)
                    if(userData?.userId == otherId){

                        binding.textViewUsername.text = userData.username
                        binding.usernameDisplay.text = "@${userData.username}"
                        binding.textViewEmail.text = userData.userEmail
                        Glide.with(this@OwnerProfileFragment)
                            .load(userData.userimage)
                            .placeholder(R.drawable.india)
                            .error(R.drawable.background2)
                            .centerCrop()

                        UserCollection.document(otherId).get().addOnSuccessListener {
                            if(it.exists()) {
                                val userDetails = it.toObject(UserDetails::class.java)
                                binding.textViewPhone.text = userDetails?.phn_no
                                binding.textRoadNo.text = userDetails?.Road_Lane
                                binding.textViewCity.text = userDetails?.City
                                binding.textViewState.text = userDetails?.State
                                binding.textViewPincode.text = userDetails?.Pincode
                                binding.textViewArea.text = userDetails?.Area
                                binding.textViewHouseNo.text = userDetails?.HouseNo
                                binding.textViewColony.text = userDetails?.colony

                                if((binding.textRoadNo.text.trim().isBlank()
                                    && binding.textViewCity.text.trim().isBlank()
                                    && binding.textViewState.text.trim().isBlank()
                                    && binding.textViewPincode.text.trim().isBlank()
                                    && binding.textViewArea.text.trim().isBlank()
                                    && binding.textViewHouseNo.text.trim().isBlank()
                                    && binding.textViewColony.text.trim().isBlank())
                                    == false
                                    ){
                                    binding.addressLayout.visibility = View.VISIBLE
                                }
                                if((binding.textViewPhone.text.trim().isBlank())==false){
                                    binding.phnLayout.visibility = View.VISIBLE
                                }
                            }
                        }

                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        setPostCount(otherId)
    }


    private fun setupProfileInfo(otherId: String) {
        setPostCount(otherId)

        binding.followersCount.text = UserData.followersCount.toString()
        binding.TenetsCount.text = UserData.tenentsCount.toString()
        binding.usernameDisplay.text = "@${UserData.username}"
        Glide.with(requireContext()).load(UserData.profilePictureUrl).into(binding.profileImage)

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
        if((binding.textRoadNo.text.trim().isBlank()
                    && binding.textViewCity.text.trim().isBlank()
                    && binding.textViewState.text.trim().isBlank()
                    && binding.textViewPincode.text.trim().isBlank()
                    && binding.textViewArea.text.trim().isBlank()
                    && binding.textViewHouseNo.text.trim().isBlank()
                    && binding.textViewColony.text.trim().isBlank())
            == false
        ){
            binding.addressLayout.visibility = View.VISIBLE
        }
        if((binding.textViewPhone.text.trim().isBlank())==false){
            binding.phnLayout.visibility = View.VISIBLE
        }
    }

    private fun setPostCount(userId: String) {
        databaseReference.child("Posts").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    UserData.postCount = snapshot.childrenCount.toInt()
                    if(_binding != null) {
                        binding.postsCount.text = UserData.postCount.toString()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setupButtons() {
        binding.editProfileButton.setOnClickListener {

        }

        binding.shareProfileButton.setOnClickListener {

        }
        binding.posts.setOnClickListener {
            var bundle: Bundle = Bundle()
            bundle.putString("uid",otherId?:firebaseUser?.uid)
            if(otherId == null){
                findNavController().navigate(R.id.action_ownerProfileFragment_to_uploadsFragment)
            }else{
                findNavController().navigate(R.id.owner_profile_fragment_to_uploads_Fragment,bundle)
            }
            activity?.findViewById<ChipNavigationBar>(R.id.bottom_nav_bar_owner)?.setItemSelected(R.id.uploadsFragment, true)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }


}