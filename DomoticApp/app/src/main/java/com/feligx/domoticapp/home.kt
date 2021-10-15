package com.feligx.domoticapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val lightButton = findViewById<com.google.android.material.card.MaterialCardView>(R.id.smartLight)
        val temperatureButton = findViewById<com.google.android.material.card.MaterialCardView>(R.id.Temperature)
        val lockButton = findViewById<com.google.android.material.card.MaterialCardView>(R.id.smartLock)
        val logOutButton = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.logout_button)

        val userInfo : Bundle? = intent.extras
        val email : String? = userInfo?.getString("email")
        val pwd : String? = userInfo?.getString("pwd")


        lightButton.setOnClickListener{
            val Intent = Intent(this, smart_light::class.java).apply{
                putExtra("email", email)
                putExtra("pwd", pwd)
            }
            startActivity(Intent)
        }

        temperatureButton.setOnClickListener{
            val Intent = Intent(this, smart_light::class.java)
            startActivity(Intent)
        }

        lockButton.setOnClickListener{
            val Intent = Intent(this, smart_light::class.java)
            startActivity(Intent)
        }

        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

    }
}