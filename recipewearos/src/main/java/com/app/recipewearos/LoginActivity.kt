package com.app.recipewearos

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.app.recipewearos.databinding.ActivityLoginBinding

class LoginActivity : Activity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = "sushant"
        val password = "sushant"
        binding.btnLogin.setOnClickListener {
            login(username, password)
        }
    }

    private fun login(username: String, password: String) {
        if (username == binding.etUsername.text.toString() && password == binding.etPassword.text.toString()) {
            startActivity(Intent(this, DashboardActivity::class.java).apply {
                putExtra("username", username)
            })
        }
    }
}