package com.feligx.domoticapp

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.TypedValue
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore

class smart_lock : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_lock)

        val lock_indicator = findViewById<ImageButton>(R.id.lock_indicator)
        val save_button = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.save_button)
        val screen_text = findViewById<ImageButton>(R.id.edit_screen_text)
        val text = findViewById<TextView>(R.id.text_displayed)
        val active_lock = findViewById<ImageButton>(R.id.lock_indicator_active)

        val dp_stroke_width = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30F, getResources().getDisplayMetrics()))
        val lock_drawable = lock_indicator.background.mutate() as LayerDrawable
        val circle_shape = lock_drawable.findDrawableByLayerId(R.id.circle_lock).mutate() as GradientDrawable
        val lock_icon = lock_drawable.findDrawableByLayerId(R.id.lock_icon)

        val userInfo : Bundle? = intent.extras
        val email : String = userInfo?.getString("email") as String
        val pwd : String? = userInfo?.getString("pwd")
        val usr : String? = userInfo?.getString("username")
        var state: Boolean = userInfo?.getBoolean("state") as Boolean

        lateinit var opened_text : String
        lateinit var closed_text : String


        db.collection("locks").document(email).get().addOnSuccessListener {
            opened_text = (it.get("text_open") as String?).toString()
            closed_text = (it.get("text_closed") as String?).toString()
            if (state == true){ //when the lock is closed
                text.setText(closed_text)
                circle_shape.setStroke(dp_stroke_width, Color.parseColor("#db2323"))
                lock_icon.setTint(Color.parseColor("#db2323"))
            }else{ //when the lock is open
                text.setText(opened_text)
                circle_shape.setStroke(dp_stroke_width, Color.parseColor("#3cdb23"))
                lock_icon.setTint(Color.parseColor("#3cdb23"))
            }
        }

        lock_indicator.setOnClickListener {
            active_lock.isVisible=true
            lock_indicator.isVisible=false
            if (state){ //when the lock is closed
                active_lock.setBackgroundResource(R.drawable.lock_circle_indicator)
                state=false
                db.collection("locks").document(email).get().addOnSuccessListener {
                    opened_text = (it.get("text_open") as String?).toString()
                }
                text.setText(opened_text)
            }else{ //when the lock is open
                active_lock.setBackgroundResource(R.drawable.lock_circle_indicator_closed)
                state=true
                db.collection("locks").document(email).get().addOnSuccessListener {
                    closed_text = (it.get("text_closed") as String?).toString()
                }
                text.setText(closed_text)
            }
            db.collection("locks").document(email).update(
                mapOf("is_locked" to state)
            )
        }

        active_lock.setOnClickListener {
            if (state){ //when the lock is closed
                active_lock.setBackgroundResource(R.drawable.lock_circle_indicator)
                state=false
                db.collection("locks").document(email).get().addOnSuccessListener {
                    opened_text = (it.get("text_open") as String?).toString()
                }
                text.setText(opened_text)
            }else{ //when the lock is open
                active_lock.setBackgroundResource(R.drawable.lock_circle_indicator_closed)
                state=true
                db.collection("locks").document(email).get().addOnSuccessListener {
                    closed_text = (it.get("text_closed") as String?).toString()
                }
                text.setText(closed_text)
            }
            db.collection("locks").document(email).update(
                mapOf("is_locked" to state)
            )
        }

        screen_text.setOnClickListener {
            db.collection("locks").document(email).get().addOnSuccessListener {
                opened_text = (it.get("text_open") as String?).toString()
                closed_text = (it.get("text_closed") as String?).toString()
                val Intent = Intent(this, edit_display_text::class.java).apply {
                    putExtra("usr", usr)
                    putExtra("email", email)
                    putExtra("pwd", pwd)
                    putExtra("state", state)
                    putExtra("text_open", opened_text)
                    putExtra("text_closed", closed_text)
                }
                startActivity(Intent)
            }
        }



        save_button.setOnClickListener {
            onBackPressed()
            finish()
        }
    }
}