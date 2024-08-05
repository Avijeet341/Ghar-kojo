package com.avi.gharkhojo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.avi.gharkhojo.Model.UserSignupLoginManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SplashScreen : AppCompatActivity() {
    private val TAG = "SplashScreen"

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        // Set the status bar color
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.statusBarColor = resources.getColor(R.color.your_status_bar_color, theme)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val videoView = findViewById<VideoView>(R.id.videoViewSplash)
        val videoPath = "android.resource://" + packageName + "/" + R.raw.splash_video
        val videoUri = Uri.parse(videoPath)
        videoView.setVideoURI(videoUri)
        videoView.setOnPreparedListener {
            videoView.start()
        }
        videoView.setOnCompletionListener {
            if(FirebaseAuth.getInstance().currentUser!=null && !FirebaseAuth.getInstance().currentUser!!.isEmailVerified){
                FirebaseAuth.getInstance().signOut()
                FirebaseAuth.getInstance().currentUser?.delete()
            }
            else if (FirebaseAuth.getInstance().currentUser != null && FirebaseAuth.getInstance().currentUser!!.isEmailVerified) {

                runBlocking {
                    UserSignupLoginManager.getInstance(this@SplashScreen).setUp()
                }
                Log.d("TAG","User data updated")
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))

            } else {
                startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
            }
            finish()
        }
    }
}
