package com.avi.gharkhojo.Adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.avi.gharkhojo.Model.SignUpViewModel
import com.avi.gharkhojo.R
import com.avi.gharkhojo.databinding.CountdownBinding
import com.google.firebase.auth.FirebaseUser

class CustomDialog(
    private val context: Context,
    private val user: FirebaseUser,
    private val button: Button,
    private var name: String,
    private var signUpViewModel: SignUpViewModel
) : DialogFragment() {

    private var countDown: CountdownBinding? = null
    init {
        setCancelable(false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        countDown = CountdownBinding.inflate(layoutInflater, null, false)
        val dialog = Dialog(requireContext()).apply {
            setContentView(countDown!!.root)
        }
        startCountDown()
        return dialog
    }

    private fun startCountDown() {
        object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                user.reload().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (user.isEmailVerified) {
                            cancel()
                            signUpViewModel.setUpUserData(name, user.email!!)
                            dismissAllowingStateLoss()

                        } else {
                            updateTimerUI(millisUntilFinished)
                        }
                    }
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFinish() {
                countDown!!.timerText.text = "00:00"
                user.delete()
                signUpViewModel._signUpState.value = SignUpViewModel.SignUpState.VerificationFailure
                dismiss()
            }
        }.start()
    }

    @SuppressLint("DefaultLocale")
    private fun updateTimerUI(millisUntilFinished: Long) {
        when {
            millisUntilFinished / 1000 < 15 -> {
                countDown!!.timerText.setTextColor(context.resources.getColor(R.color.dialogMessageColorRed))
            }
            millisUntilFinished / 1000 < 30 -> {
                countDown!!.timerText.setTextColor(context.resources.getColor(R.color.dialogMessageColorYellow))
            }
            else -> {
                countDown!!.timerText.setTextColor(context.resources.getColor(R.color.dialogMessageColorGreen))
            }
        }

        countDown!!.timerText.text = String.format(
            "%02d:%02d",
            millisUntilFinished / 1000 / 60,
            millisUntilFinished / 1000 % 60
        )
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        countDown = null
    }
}
