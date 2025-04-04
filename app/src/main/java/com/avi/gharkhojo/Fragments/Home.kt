package com.avi.gharkhojo.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Adapter.GridAdapter
import com.avi.gharkhojo.Adapter.HousingTypeAdapter
import com.avi.gharkhojo.Model.HousingType
import com.avi.gharkhojo.Model.Post
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.OwnerActivity
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentHomeBinding
import com.bumptech.glide.RequestManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList
import java.util.Arrays
import javax.inject.Inject

@AndroidEntryPoint
class Home : Fragment() {
    private var sortOption: String? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var filterAnimation: android.view.animation.Animation
    @Inject lateinit var requestManager: RequestManager
    private var databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Posts")
    private var areasList:ArrayList<String> = ArrayList();
    private var filterPost: Post? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserProfile()
        setupToolbar()
        setupGridView()
        setupSearchView()
        setupFilterButtonAnimation()
        observeDataChanges()

        parentFragmentManager.setFragmentResultListener("filterResult", this) { _, result ->
             filterPost = result.getParcelable<Post>("filterPost")
             sortOption = result.getString("sortOption")
            areasList = result.getStringArrayList("areas")?: ArrayList()
            observeDataChanges()
        }


    }

    private fun setupFilterButtonAnimation() {
        filterAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.filter_button_animation)

        binding.filterButton.setOnClickListener {
            it.startAnimation(filterAnimation)
            findNavController().navigate(R.id.action_home2_to_filterFragment)
        }
    }

    private fun setupSearchView() {
        val searchView = binding.toolbar.findViewById<SearchView>(R.id.search_view)

        // Change text color
        val searchText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchText.setTextColor(ContextCompat.getColor(requireContext(), R.color.expBlue))
        searchText.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.expBlue))
    }

    private fun setupUserProfile() {
        UserData.profilePictureUrl?.let { url ->
            requestManager.load(url).placeholder(R.drawable.vibe).into(binding.userImage)
        } ?: run {
            binding.userImage.setImageResource(R.drawable.vibe)
        }
        binding.username.text = UserData.username ?: getString(R.string.default_username)
    }

    private fun setupToolbar() {
        val recyclerView: RecyclerView = binding.toolbar.findViewById(R.id.housingTypeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val housingTypes = listOf(
            HousingType(R.drawable.ic_baseline_add_24, "Add Property"),
            HousingType(R.drawable.home, "House"),
            HousingType(R.drawable.apartment, "Apartment"),
            HousingType(R.drawable.building, "Flat"),
            HousingType(R.drawable.dormitory, "Dormitory"),
            HousingType(R.drawable.luxury, "Luxury"),
            HousingType(R.drawable.commercial_property, "Commercial")
        )

        val adapter = HousingTypeAdapter(housingTypes, {
            val intent = Intent(requireContext(), OwnerActivity::class.java)
            startActivity(intent)
            this.requireActivity().finish()
        },
            { housingType ->
                observeDataChanges(housingType)
            })
        recyclerView.adapter = adapter
    }

    private fun setupGridView() {
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = GridAdapter { post ->
                val action = HomeDirections.actionHome2ToHomeDetails()
                val bundle = Bundle().apply {
                    putParcelable("post", post)
                }
                action.arguments.putAll(bundle)

                findNavController().navigate(action)
            }
        }
    }

    private fun observeDataChanges(filter: MutableList<String>? = null) {
        Log.d("h", filter.toString())
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Null check before accessing binding
                if (_binding == null) {
                    return
                }
                if (snapshot.exists()) {
                    val mutableList: MutableList<Post> = mutableListOf()
                    for (dataSnapshot in snapshot.children) {
                        for (data in dataSnapshot.children) {
                            val post = data.getValue(Post::class.java)
                            if (post != null) {
                                if (filter != null) {
                                    filterPost = null
                                    if (filter.contains(post.propertyType)) {
                                        mutableList.add(post)
                                    }
                                }
                                if(filter.isNullOrEmpty()){
                                    if(filterPost!=null){

                                        Log.d("preferTent", filterPost!!.preferredTenants.toString())
                                        Log.d("propertyType",filterPost!!.propertyType.toString())
                                        Log.d("bedroom",filterPost?.noOfBedRoom.toString())
                                        Log.d("bathroom",filterPost?.noOfBathroom.toString())
                                        Log.d("balcony",filterPost?.noOfBalcony.toString())
                                        Log.d("floor",filterPost?.floorPosition.toString())
                                        Log.d("filter",filterPost?.hasLift.toString())
                                        Log.d("filter",filterPost?.hasGenerator.toString())
                                        Log.d("filter",filterPost?.hasGasService.toString())
                                        Log.d("filter",filterPost?.hasSecurityGuard.toString())
                                        Log.d("filter",filterPost?.hasParking.toString())

                                        Log.d("builtUpArea",filterPost?.builtUpArea.toString())
                                        Log.d("rent",filterPost?.rent.toString())


                                        var postBuiltUpArea = currencyToFloat(post.builtUpArea!!)
                                        var filterFloorPosition = filterPost!!.floorPosition?.toInt()
                                        var postRent = post.rent?.let { currencyToFloat(it) }
                                        var filterBudget:List<Float> = filterPost!!.rent.toString().split("-").map { currencyToFloat(it) }
                                        var filterBuiltUpArea:List<Float> = filterPost!!.builtUpArea.toString().split("-").map { currencyToFloat(it) }
                                        var postFloorPosition = post.floorPosition?.toInt()

                                        if(((postRent!! in (filterBudget[0]..filterBudget[1])) ||
                                                    ((filterBudget[1].toInt() > 50000) && (postRent > filterBudget[1]))) &&
                                            ((postBuiltUpArea in (filterBuiltUpArea[0]..filterBuiltUpArea[1]))
                                                    || ((filterBuiltUpArea[1].toInt() > 3000) && (postBuiltUpArea > filterBuiltUpArea[1])))
                                            && ((postFloorPosition!! == filterFloorPosition!!)
                                                    || (filterFloorPosition == -1))
                                            && ((post.preferredTenants == filterPost?.preferredTenants)
                                                    || (filterPost?.preferredTenants == "Any"))
                                            && ((post.propertyType == filterPost?.propertyType) || (filterPost?.propertyType == "Any"))
                                            && ((post.noOfBedRoom == filterPost?.noOfBedRoom) || (filterPost?.noOfBedRoom == -1))
                                            && ((post.noOfBathroom == filterPost?.noOfBathroom) || (filterPost?.noOfBathroom == -1))
                                            && ((post.noOfBalcony == filterPost?.noOfBalcony) || (filterPost?.noOfBalcony == -1))
                                            && ((post.noOfKitchen == filterPost?.noOfKitchen) || (filterPost?.noOfKitchen == -1))
                                            && ((filterPost?.hasLift == null) || ((filterPost?.hasLift == true) && (post.hasLift == true)))
                                            && ((filterPost?.hasGenerator == null) || ((filterPost?.hasGenerator == true) && (post.hasGenerator == true)))
                                            && ((filterPost?.hasGasService == null) || ((filterPost?.hasGasService == true) && (post.hasGasService == true)))
                                            && ((filterPost?.hasSecurityGuard == null) || ((filterPost?.hasSecurityGuard == true) && (post.hasSecurityGuard == true)))
                                            && ((filterPost?.hasParking == null) || ((filterPost?.hasParking == true) && (post.hasParking == true)))
                                            && (areasList.isEmpty() || areasList.contains(post.area!!.uppercase()))){

                                                Log.d("postRent", postRent.toString())

                                                mutableList.add(post)
                                            }
                                    }else{
                                        mutableList.add(post)
                                    }

                                    if(!sortOption.isNullOrEmpty() && sortOption == "l-h"){
                                        mutableList.sortBy { currencyToFloat(it.rent!!) }
                                    }
                                    else{
                                        mutableList.sortByDescending { currencyToFloat(it.rent!!) }
                                    }

                                }


                            }
                        }
                    }
                    (binding.recyclerView.adapter as? GridAdapter)?.updateData(mutableList)
                }
                binding.loadDataProgress.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Null check before accessing binding
                if (_binding == null) {
                    return
                }
                Toast.makeText(context, "Failed to load data: ${error.message}", Toast.LENGTH_SHORT).show()
                binding.loadDataProgress.visibility = View.GONE
            }
        })
    }
    fun currencyToFloat(currencyString: String): Float {
        val cleanString = currencyString.replace("[^\\d.]".toRegex(), "")
        return cleanString.toFloat()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
