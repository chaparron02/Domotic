package com.feligx.domoticapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class home : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val db_realtime = FirebaseDatabase.getInstance()

    private val locks_reference = db_realtime.getReference("locks")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val usernameText = findViewById<TextView>(R.id.username)
        val lightButton = findViewById<com.google.android.material.card.MaterialCardView>(R.id.smartLight)
        val temperatureButton = findViewById<com.google.android.material.card.MaterialCardView>(R.id.Temperature)
        val lockButton = findViewById<com.google.android.material.card.MaterialCardView>(R.id.smartLock)
        val logOutButton = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.logout_button)

        val userInfo : Bundle? = intent.extras
        val email : String? = userInfo?.getString("email")
        val pwd : String? = userInfo?.getString("pwd")
        val usr : String = userInfo?.getString("username").toString()

        usernameText.setText(usr + "'s")

        lightButton.setOnClickListener{
            val Intent = Intent(this, smart_light::class.java).apply{
                putExtra("email", email)
                putExtra("pwd", pwd)
                putExtra("usr", usr)
            }
            startActivity(Intent)
        }

        temperatureButton.setOnClickListener{
            val Intent = Intent(this, smart_regulator::class.java).apply{
                putExtra("email", email)
                putExtra("pwd", pwd)
                putExtra("usr", usr)
            }
            startActivity(Intent)
        }

        lockButton.setOnClickListener{

            var locked : Boolean?
            if (email != null) {
                locks_reference.child(usr).get().addOnSuccessListener {
                    locked = it.child("is_locked").value as Boolean
                    val Intent = Intent(this, smart_lock::class.java).apply{
                        putExtra("usr", usr)
                        putExtra("email", email)
                        putExtra("pwd", pwd)
                        putExtra("state", locked)
                    }
                    startActivity(Intent)
                }
            }
        }

        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }

    }
}