package com.example.bloodbank006_3

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.bloodbank006_3.data.ClientDatabase

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {

    private lateinit var clientDatabase: ClientDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()

        clientDatabase = ClientDatabase.getInstance(applicationContext)!!

        handlerLoop()
    }

    private fun handlerLoop() {
        Handler(Looper.getMainLooper()).postDelayed({
            val loggedInUserEmailLiveData =
                clientDatabase.appDatabase.formDao().getLoggedInUserEmail()
            loggedInUserEmailLiveData.observe(this) { loggedInUserEmail ->
                val intent = if (loggedInUserEmail.isNullOrEmpty()) {
                    // User is not logged in, redirect to LoginActivity
                    Intent(this, MainActivity::class.java)
                } else {
                    // User is already logged in, redirect to MainActivity
                    Intent(this, MainActivity::class.java)
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finishAffinity()
            }
        }, 1000L)
    }
}
