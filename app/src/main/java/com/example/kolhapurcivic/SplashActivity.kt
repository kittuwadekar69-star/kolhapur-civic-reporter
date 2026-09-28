package com.example.kolhapurcivic

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({

            val auth = FirebaseAuth.getInstance()

            val intent = if (auth.currentUser != null) {

                Intent(this, HomeActivity::class.java)

            } else {

                Intent(this, MainActivity::class.java)
            }

            startActivity(intent)
            finish()

        }, 2500)
    }
}
