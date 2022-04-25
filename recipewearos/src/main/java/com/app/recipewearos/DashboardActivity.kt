package com.app.recipewearos

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.recipewearos.databinding.ActivityDashboardBinding
import com.app.recipewearos.databinding.ActivityLoginBinding

class DashboardActivity : Activity() {
    private lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tv.text = "Welcome ${intent.getStringExtra("username")}"
    }
}