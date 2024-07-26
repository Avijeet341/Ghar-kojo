package com.avi.gharkhojo

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.avi.gharkhojo.Model.ChatUserListModel
import com.avi.gharkhojo.Model.LoginViewModel
import com.avi.gharkhojo.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var viewModel: LoginViewModel



    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        setupWindowInsets()
        setupUI()
        observeViewModel()
        startLoginBgAnimation()

        googleSignInClient = configureGoogleSignIn()
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    handleGoogleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(result.data))
                }
            }
        )
    }

    private fun startLoginBgAnimation() {
        val constraintLayout: ConstraintLayout = findViewById(R.id.LoginBgLayout)
        val animationDrawable: AnimationDrawable = constraintLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(1000)
        animationDrawable.setExitFadeDuration(2000)
        animationDrawable.start()
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
        loginBinding.apply {
            buttonLogin.setOnClickListener {
                val email = editTextTextEmailAddress.text.toString()
                val pass = editTextTextPassword.text.toString()
                showLoading()
                viewModel.signInUser(email, pass,this@LoginActivity)
            }
            buttonGoogle.setOnClickListener { signInGoogle() }
            buttonFacebook.setOnClickListener {
                // Add Facebook login logic here
            }
            SignUptextView.setOnClickListener { navigateTo(SignUpActivity::class.java) }
            forgetMyPasstextView.setOnClickListener { navigateTo(ForgotActivity::class.java) }
        }
    }

    private fun showLoading() {
        loginBinding.apply {
            buttonLogin.visibility = View.GONE
            progressCircularLogin.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        loginBinding.apply {
            buttonLogin.visibility = View.VISIBLE
            progressCircularLogin.visibility = View.GONE
        }
    }

    private fun observeViewModel() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginViewModel.LoginState.Success -> {
                    hideLoading()
                    showToast("Welcome Guys 💕🎇🎉🎊")
                    navigateTo(MainActivity::class.java)
                }
                is LoginViewModel.LoginState.Error -> {
                    hideLoading()
                    showToast(state.message)
                }
                LoginViewModel.LoginState.Idle -> { /* Do nothing */ }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.isUserLoggedIn()) {
            showToast("Welcome Guys😎")
            navigateTo(MainActivity::class.java)
        }
    }

    private fun signInGoogle() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)
    }

    private fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            if (account != null) {
                viewModel.firebaseGoogleAccount(account)
            } else {
                showToast("Google sign-in failed.")
            }
        } catch (e: ApiException) {
            showToast(e.localizedMessage ?: "Google sign-in failed.")
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

    private fun configureGoogleSignIn(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(this, gso)
    }
}