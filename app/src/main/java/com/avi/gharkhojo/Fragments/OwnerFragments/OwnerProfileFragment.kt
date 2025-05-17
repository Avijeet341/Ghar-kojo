package com.avi.gharkhojo.Fragments.OwnerFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentOwnerProfileBinding

class OwnerProfileFragment : Fragment() {

    private var _binding: FragmentOwnerProfileBinding? = null
    private val binding get() = _binding!!

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
        // Profile stats
        binding.postsCount.text = "23"
        binding.followersCount.text = "79"
        binding.TenetsCount.text = "151"
        binding.usernameDisplay.text = "@username"

        // Contact information
        binding.textViewEmail.text = "user@example.com"
        binding.textViewUsername.text = "username"
        binding.textViewPhone.text = "+91 9876543210"

        // Address information
        binding.textViewHouseNo.text = "123"
        binding.textRoadNo.text = "Lane 4"
        binding.textViewColony.text = "Green Park"
        binding.textViewArea.text = "South Extension"
        binding.textViewCity.text = "New Delhi"
        binding.textViewState.text = "Delhi"
        binding.textViewPincode.text = "110001"
    }

    private fun setupButtons() {
        binding.editProfileButton.setOnClickListener {

        }

        binding.shareProfileButton.setOnClickListener {

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