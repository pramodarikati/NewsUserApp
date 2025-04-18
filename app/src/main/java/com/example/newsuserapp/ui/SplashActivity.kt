package com.example.newsuserapp.ui

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.example.newsuserapp.R
import com.example.newsuserapp.databinding.ActivitySplashBinding
import com.example.newsuserapp.ui.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                window.statusBarColor = getColor(android.R.color.black)
                window.decorView.systemUiVisibility = 0
            } else {
                window.statusBarColor = getColor(android.R.color.white)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }


        setupVideoView()

        binding.root.postDelayed({
            checkUserAuthentication()
        }, 3000)
    }

    private fun setupVideoView() {
        val videoView = binding.videoView
        val videoPath = "android.resource://${packageName}/${R.raw.news_intro}"
        videoView.setVideoPath(videoPath)

        videoView.setOnPreparedListener { mp ->
            mp.isLooping = false
            videoView.start()

            binding.videoView.postDelayed({
                videoView.stopPlayback()
            }, 7000)
        }
    }


    private fun checkUserAuthentication() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            navigateToMainActivity()
        } else {
            navigateToLoginActivity()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}