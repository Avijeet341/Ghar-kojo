package com.avi.gharkhojo.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.avi.gharkhojo.LoginActivity
import com.avi.gharkhojo.Model.SharedViewModel
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentProfileBinding
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class Profile : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val firebaseAuth:FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this)
            .load(firebaseAuth.currentUser?.photoUrl)
            .placeholder(R.drawable.india)
            .error(R.drawable.background2)
            .into(binding.ProfilePic)

        binding.buttonSignOut.setOnClickListener {
            signOut()
        }

        binding.textViewUsername.text = firebaseAuth.currentUser?.let {
            if(it.displayName.isNullOrEmpty()) {
                "UserName"
            }
            else{
                it.displayName
            }
         }.toString()
    }

    private fun signOut() {
        // Sign out from Firebase Auth
        FirebaseAuth.getInstance().signOut()

        // Sign out from Google account
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        googleSignInClient.signOut().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Sign Out is successful ✅💕👍", Toast.LENGTH_SHORT).show()
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), "Sign Out failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
