package com.avi.gharkhojo.Fragments.OwnerFragments

import android.R.attr.action
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.media3.common.util.Log
import androidx.navigation.fragment.findNavController
import com.avi.gharkhojo.Chat.ChatRoom
import com.avi.gharkhojo.Fragments.Followers
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
            binding.followBtn.visibility = View.VISIBLE
            binding.msgBtn.visibility = View.VISIBLE
            bottomNav?.visibility = View.GONE
            setUpFollowInfo(otherId.toString())
        }else{
            binding.followBtn.visibility = View.GONE
            binding.msgBtn.visibility = View.GONE
            bottomNav?.visibility = View.VISIBLE
            bottomNav?.setItemSelected(R.id.nav_profile, true)
            setupProfileInfo(firebaseUser!!.uid)
            setUpFollowInfo(firebaseUser.uid)

        }

        setupButtons()
    }

    private fun setUpFollowInfo(uid: String) {
        CoroutineScope(Dispatchers.Main).launch{
            databaseReference.child("users")
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (_binding == null) {
                            return
                        }
                        if (snapshot.exists()) {
                            var isAvailable: Boolean = false
                            for (dataSnapshot in snapshot.children) {
                                val userData = dataSnapshot.getValue(ChatUserListModel::class.java)
                                if (userData?.userId == uid) {
                                    var count: Long = 0
                                    if(dataSnapshot.hasChild("followers"))
                                    {
                                        count = dataSnapshot.child("followers").childrenCount
                                        isAvailable = dataSnapshot.child("followers").children.any { it.value == firebaseUser?.uid }
                                    }
                                    binding.followersCount.text = count.toString()
                                    break
                                }

                            }
                            if (isAvailable) {
                                binding.followBtn.text = "Unfollow"
                            } else {
                                binding.followBtn.text = "Follow"
                            }
                            setUpFollowersButton()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                    }

                })
        }


    }

    private fun loadOtherUserProfile(otherId: String) {

        databaseReference.child("users").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(_binding==null){
                    return
                }
                for(dataSnapshot in snapshot.children){
                    val userData = dataSnapshot.getValue(ChatUserListModel::class.java)
                    if(userData?.userId == otherId){

                        binding.textViewUsername.text = userData.username
                        binding.usernameDisplay.text = "@${userData.username}"
                        binding.textViewEmail.text = userData.userEmail
                        Glide.with(this@OwnerProfileFragment)
                            .load(userData.userimage)
                            .placeholder(R.drawable.baseline_person_24)
                            .error(R.drawable.baseline_person_24)
                            .centerCrop()
                            .into(binding.profileImage)

                        UserCollection.document(otherId).get().addOnSuccessListener {
                            if(it.exists()) {
                                if(_binding == null || it == null){
                                    return@addOnSuccessListener
                                }

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

    fun setUpFollowersButton(){
        val count = binding.followersCount.text.toString().trim().toInt()
        if(count>0){
            binding.followersCount.isClickable = true
            binding.followersCount.isEnabled = true
            binding.followersCount.setOnClickListener {
                findNavController().navigate(R.id.action_ownerProfileFragment_to_followersFragment, Bundle().also {
                    it.putString(Followers.UID, firebaseUser?.uid)
                })
                activity?.findViewById<ChipNavigationBar>(R.id.bottom_nav_bar_owner)?.visibility = View.GONE
            }
        }
        else{
            binding.followersCount.isClickable = false
            binding.followersCount.isEnabled = false
        }
    }
    private fun setupButtons() {
        binding.msgBtn.setOnClickListener {
            startActivity(Intent(requireContext(), ChatRoom::class.java).also {
                it.putExtra(ChatRoom.UID_ARG,otherId)
            })
        }

        binding.followBtn.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                binding.followBtn.isEnabled = false
                databaseReference.child("users")
                    .get().addOnCompleteListener { task ->
                        if (task.isSuccessful && task.result.exists()) {
                            for (dataSnapshot in task.result.children) {
                                val chatUserList = dataSnapshot.getValue(ChatUserListModel::class.java)
                                if (chatUserList?.userId == otherId) {

                                    val followers = chatUserList?.followers?.toMutableList() ?: mutableListOf()

                                    val currentUserId = firebaseUser?.uid ?: return@addOnCompleteListener

                                    if (followers.contains(currentUserId)) {
                                        followers.remove(currentUserId)
                                        dataSnapshot.ref.child("followers").setValue(followers)
                                            .addOnSuccessListener {
                                                binding.followBtn.text = "Follow"
                                                binding.followBtn.isEnabled = true

                                            }
                                    } else {
                                        followers.add(currentUserId)
                                        dataSnapshot.ref.child("followers").setValue(followers)
                                            .addOnSuccessListener {
                                                binding.followBtn.text = "Unfollow"
                                                binding.followBtn.isEnabled = true

                                            }
                                    }

                                    break
                                }
                            }
                        }
                    }.await()
                binding.followBtn.isEnabled = true
            }

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