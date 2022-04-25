package com.app.merorecipe.ui

import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import com.app.merorecipe.R
import kotlinx.coroutines.*

class SplashActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private val activityScope = CoroutineScope(Dispatchers.Main);
    private val CURRENT_PROGRESS = 10
    private val DELAY = 1500L
    private val MAX_VALUE = 10

    companion object {
        var isLoggedIn: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        progressBar = findViewById(R.id.progressBarHorizontal)
        loadSplash()
        var i = 0;
        while (i <= DELAY) {
            loadProgress()
            i++
        }


    }

    private fun getLoggedInStatus() {
        val sharedPreferences = getSharedPreferences("MYPref", MODE_PRIVATE)
        sharedPreferences.apply {
            if (getBoolean("IsLoggedIn", true))
                startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
        }

    }


    private fun loadProgress() {

        progressBar.max = MAX_VALUE
        ObjectAnimator
            .ofInt(progressBar, "Progress", CURRENT_PROGRESS)
            .setDuration(DELAY)
            .start()
    }

    private fun loadSplash() {
        activityScope.launch {
            delay(DELAY)
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        }
    }


}