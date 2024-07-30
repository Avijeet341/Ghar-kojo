
package com.avi.gharkhojo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.avi.gharkhojo.Model.SignUpViewModel
import com.avi.gharkhojo.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private val signUpBinding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }



    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(signUpBinding.root)

        setupWindowInsets()
        viewModel = ViewModelProvider(this).get(SignUpViewModel::class.java)
        setupUI()
        observeViewModel()
    }

    private fun setupWindowInsets() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.your_status_bar_color)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupUI() {
        signUpBinding.buttonSignUp.setOnClickListener {
            val name = signUpBinding.usernameSignUp.text.toString().trim()
            val email = signUpBinding.editTextViewEmailSignUP.text.toString().trim()
            val pass = signUpBinding.editTextViewPasswordSignUP.text.toString().trim()
            val confirmPass = signUpBinding.editTextViewConfirmPasswordSignUP.text.toString().trim()
            viewModel.signUpWithFirebase(name,email, pass, confirmPass)
        }
    }

    private fun observeViewModel() {
        viewModel.signUpState.observe(this) { state ->
            when (state) {
                is SignUpViewModel.SignUpState.Loading -> {
                    signUpBinding.progressCircularSignUp.visibility = View.VISIBLE
                    signUpBinding.buttonSignUp.isClickable = false
                }
                is SignUpViewModel.SignUpState.Success -> {
                    showToast("Your account has been created. ✅👍😍")
                    navigateTo(LoginActivity::class.java)
                    signUpBinding.progressCircularSignUp.visibility = View.INVISIBLE
                    signUpBinding.buttonSignUp.isClickable = true

                }
                is SignUpViewModel.SignUpState.Error -> {
                    showToast(state.message)
                    signUpBinding.progressCircularSignUp.visibility = View.INVISIBLE
                    signUpBinding.buttonSignUp.isClickable = true
                }
                SignUpViewModel.SignUpState.Idle -> {
                    // Do nothing or initial state handling if needed
                }
            }
        }
    }

    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
