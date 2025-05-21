package com.avi.gharkhojo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.avi.gharkhojo.Chat.ChatRoom
import com.avi.gharkhojo.Model.UserSignupLoginManager
import com.avi.gharkhojo.databinding.ActivitySplashScreenBinding
import com.avi.gharkhojo.notifications.MyFirebaseMessagingService
import com.avi.gharkhojo.notifications.NotificationConstant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SplashScreen : AppCompatActivity() {

    private val firebaseUser: FirebaseUser? by lazy { FirebaseAuth.getInstance().currentUser }

    var _binding: ActivitySplashScreenBinding? = null
    val binding get() = _binding!!

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupEdgeToEdge()
//        setupVideoView()

        CoroutineScope(Dispatchers.IO).launch {
            firebaseUser?.let { user ->
                try {

                    reloadUser(user)
                    UserSignupLoginManager.getInstance(this@SplashScreen).setUp()
                    handleUserReload(user)
                    FirebaseMessaging.getInstance().token
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("Token", it.result)
                                var token: String = it.result
                                FirebaseDatabase.getInstance().reference.child("Tokens")
                                    .child(user.uid).child("token").setValue(token)

                                return@addOnCompleteListener
                            }

                        }


                } catch (e: Exception) {

                    navigateToLogin()
                }
            } ?: run { navigateToLogin() }

        }

        // for notification:
        var policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        binding.aashiyana.text = ""
        var text = "Aashiyana"
        var index = 0
        val handler = Handler(Looper.getMainLooper())

        val runnable = object : Runnable {
            override fun run() {
                if (index < text.length) {
                    binding.aashiyana.append(text[index].toString())
                    index++
                    handler.postDelayed(this, 150)
                } else {
                    index = 0
                    binding.aashiyana.text = ""
                    handler.postDelayed(this, 150)
                }
            }
        }
        handler.post(runnable)
    }

    private fun setupStatusBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.statusBarColor = resources.getColor(R.color.your_status_bar_color, theme)
    }

    private fun setupEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private suspend fun reloadUser(user: FirebaseUser) {
        withContext(Dispatchers.IO) { user.reload().await() }
    }

    private fun handleUserReload(user: FirebaseUser) {
        if (user.isEmailVerified) {

            if (intent.getBooleanExtra(NotificationConstant.MESSAGE_NOTIFICATION.value, false)) {
                Toast.makeText(this, "Message Notification", Toast.LENGTH_SHORT).show()


                val intent = Intent(this, ChatRoom::class.java)
                intent.putExtra(ChatRoom.IMG_ARG, intent.getStringExtra(ChatRoom.IMG_ARG))
                intent.putExtra(ChatRoom.NAME_ARG, intent.getStringExtra(ChatRoom.NAME_ARG))
                intent.putExtra(ChatRoom.UID_ARG, intent.getStringExtra(ChatRoom.UID_ARG))
                startActivity(intent)
                finish()
                return
            }
            navigateToLastUsedActivity()
        } else {

            signOutAndNavigateToLogin()
        }
    }

    private fun navigateToLastUsedActivity() {
        val intent = getLastUsedActivityIntent()
        startActivity(intent)
        finishAffinity()
    }

    private fun navigateToLogin() {

        startActivity(Intent(this, LoginActivity::class.java))
        finishAffinity()
    }

    private fun signOutAndNavigateToLogin() {

        FirebaseAuth.getInstance().signOut()
        firebaseUser?.delete()
        navigateToLogin()
    }

    private fun getLastUsedActivityIntent(): Intent {
        val sharedPref = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val lastUsedActivity = sharedPref.getString("lastUsedActivity", null)

        return when (lastUsedActivity) {
            "com.avi.gharkhojo.MainActivity" -> Intent(this, MainActivity::class.java)
            "com.avi.gharkhojo.OwnerActivity" -> Intent(this, OwnerActivity::class.java)
            else -> Intent(this, MainActivity::class.java) // Default to MainActivity
        }
    }
}
