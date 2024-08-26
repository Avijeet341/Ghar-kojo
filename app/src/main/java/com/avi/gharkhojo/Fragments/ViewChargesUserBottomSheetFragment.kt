package com.avi.gharkhojo.Fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.FragmentViewChargesUserBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ViewChargesUserBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentViewChargesUserBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var rent: String
    private lateinit var parkingCharge: String
    private lateinit var maintenanceCharge: String
    private lateinit var deposit: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewChargesUserBottomSheetBinding.inflate(inflater, container, false)

        // Initialize values with default values
        rent = "0"
        parkingCharge = "0"
        maintenanceCharge = "0"
        deposit = "0"

        // Retrieve arguments if they are set
        arguments?.let {
            rent = it.getString("rent", "")
            parkingCharge = it.getString("parkingCharge", "")
            maintenanceCharge = it.getString("maintenanceCharge", "")
            deposit = it.getString("deposit", "")
        }

        // Set values to the text views
        binding.rentalTextView.text = rent
        binding.parkingChargesTextView.text = parkingCharge
        binding.maintenanceChargesTextView.text = maintenanceCharge
        binding.refundableDepositTextView.text = deposit

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cancelButton.setOnClickListener {
            dismiss() // Dismiss the bottom sheet when the cancel button is clicked
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        bottomSheetDialog.setOnShowListener { dialog ->
            val d = dialog as BottomSheetDialog
            val bottomSheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val background = ResourcesCompat.getDrawable(resources, R.drawable.view_bottomsheetbg, null)
                it.background = background
            }
        }
        return bottomSheetDialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ViewChargesUserBottomSheetFragment"

        fun newInstance() = ViewChargesUserBottomSheetFragment()
    }
}
