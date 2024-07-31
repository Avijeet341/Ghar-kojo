package com.avi.gharkhojo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        // Set the status bar color
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
            if (FirebaseAuth.getInstance().currentUser != null) {
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
            } else {
                startActivity(Intent(this@SplashScreen, LoginActivity::class.java))
            }
            finish()
        }
    }
}
