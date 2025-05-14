package com.avi.gharkhojo.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.avi.gharkhojo.databinding.FragmentOwnerProfileForUserBinding

class OwnerProfileForUserFragment : Fragment() {

    private var _binding: FragmentOwnerProfileForUserBinding? = null

    private val binding get() = _binding!!

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOwnerProfileForUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        with(binding) {

            // Contact information section
            textViewEmail.text = "example@email.com"
            textViewUsername.text = "Username"
            textViewPhone.text = "+1 234 567 8900"

            // Address section
            textViewHouseNo.text = "123"
            textRoadNo.text = "Lane 5"
            textViewColony.text = "Green Colony"
            textViewArea.text = "Downtown"
            textViewLandmark.text = "Near City Park"
            textViewCity.text = "Metropolis"
            textViewState.text = "State"
            textViewPincode.text = "100001"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OwnerProfileForUserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}