package com.app.merorecipe.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.merorecipe.R
import com.app.merorecipe.api.ServiceBuilder
import com.app.merorecipe.global.GlobalClass
import com.app.merorecipe.databinding.ActivityLoginBinding
import com.app.merorecipe.entity.User
import com.app.merorecipe.notification.NotificationChannel
import com.app.merorecipe.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class LoginActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityLoginBinding
    private var activityScope = CoroutineScope(Dispatchers.IO)
    private var PREF_NAME: String = "MyPref"

    private lateinit var sensorManager: SensorManager;
    private var sensor: Sensor? = null;

    companion object {
        var UserId: String = ""
        var Username: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.hide()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager;
        if (!checkSensor()) {
            return
        } else {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensorManager.registerListener(
                this@LoginActivity,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            );
        }

        binding.btnLogin.setOnClickListener {
            if (checkEmptyFields()) {
                doLogin()

            }
        }

        binding.tvRegister.setOnClickListener {
            goToRegister()

        }
        getSharedPreferences()
    }

    private fun goToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    //This will be of boolean return type
    private fun doLogin() {
        val username = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        activityScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val getRepo = UserRepository()
                    val getResponse = getRepo.login(username, password)

                    ServiceBuilder.token = "Bearer " + getResponse.token
                    LoginActivity.UserId = getResponse?.data?._id.toString()
                    Username = getResponse?.data?.username.toString()
                    if (getResponse.success == true) {
                        withContext(Main) {
                            val firstname = getResponse.data?.firstName
                            setValueToSharedPref()
                            Notification("${getResponse.data?.firstName} ${getResponse.data?.lastName}")
                            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                        }
                    }
                } catch (ex: IOException) {
                    withContext(Main) {
                        GlobalClass.globalToast(
                            this@LoginActivity,
                            "Login Failed: Either Username of Password is Incorrect"
                        )
                    }
                }
            }
        }
    }


    fun String.isValidEmail(): Boolean =
        !TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches()

    private fun checkEmptyFields(): Boolean {
        var flag = false
        when {
            TextUtils.isEmpty(binding.etEmail.text.toString()) -> {
                binding.etEmail.error = "Email is required to login"
                binding.etEmail.requestFocus()
                vibrateOnError()
                return flag
            }
            TextUtils.isEmpty(binding.etPassword.text.toString()) -> {
                binding.etPassword.error = "Password is required to login"
                binding.etPassword.requestFocus()
                vibrateOnError()
                return flag
            }

            else -> {
                flag = true
                return flag
            }
        }
    }

    private fun vibrateOnError() {
        val vibrator = this?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(2000)
        }
    }


    private fun setValueToSharedPref() {
        val username = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()
        setSharedPreferences(username, password)
    }

    private fun setSharedPreferences(username: String, password: String) {
        val sharedPref = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        SplashActivity.isLoggedIn = true
        sharedPref.apply {
            edit()
                .putString("Username", username)
                .putString("Password", password)
                .putBoolean("IsLoggedIn", SplashActivity.isLoggedIn)

                .apply()
        }
    }

    private fun getSharedPreferences() {
        val sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
        sharedPreferences.apply {
            binding.etEmail.setText(getString("Username", ""))
            binding.etPassword.setText(getString("Password", ""))
        }
    }

    private fun Notification(user: String) {

        val manager = NotificationManagerCompat.from(this);
        val channels = NotificationChannel(this);
        channels.createNotificationChannels();
        val notification = NotificationCompat.Builder(this, channels.CHANNEL_1)
            .setSmallIcon(R.drawable.ic_app_notification)
            .setContentTitle("Hi There !")
            .setContentText("Hey ${user},  Welcome to Mero Recipe !")
            .setColor(Color.GREEN)
            .build();
        manager.notify(1, notification);

    }

    private fun checkSensor(): Boolean {
        var flag = true;
        if (sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) == null) {
            flag = false
        }
        return flag;
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val values = event!!.values[0];
        if (values <= 1) {
            val builder = AlertDialog.Builder(this);
            builder.setTitle("MisOperation Mode Triggered")
            builder.setMessage("Do not block the top of the screen");
            builder.setIcon(android.R.drawable.ic_dialog_alert);

            var alert = builder.create();
            alert.setCancelable(true);
            alert.show();
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}