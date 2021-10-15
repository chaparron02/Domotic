package com.feligx.domoticapp

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.google.firebase.firestore.FirebaseFirestore

class smart_light : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    fun backGroundColor() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.navigationBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        window.setBackgroundDrawableResource(R.drawable.bg_toolbar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_light)

        val checkButton = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.save_button)
        val addColorBttn = findViewById<ImageButton>(R.id.imageButton)
        val currentColorSquare = findViewById<View>(R.id.currentColorSquare)
        val lightSwitch = findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.light_switch)

        var current_hexcolor = "#FFFFF"
        var current_color = 0
        var panel_is_on = lightSwitch.isChecked()

        //Gather info from intent
        val userInfo : Bundle? = intent.extras
        val email : String? = userInfo?.getString("email")
        val pwd : String? = userInfo?.getString("pwd")
        if (email != null) {
            db.collection("led_panels").document(email).get().addOnSuccessListener {
                current_hexcolor= (it.get("hex_color") as String?).toString()
                if (current_hexcolor == "#FFFFF" || current_hexcolor == "#fffff"){
                    current_hexcolor=current_hexcolor.capitalize()
                    currentColorSquare.background.setColorFilter(Color.parseColor(current_hexcolor+"F"), PorterDuff.Mode.SRC_ATOP)
                }else {
                    currentColorSquare.background.setColorFilter(
                        Color.parseColor(current_hexcolor),
                        PorterDuff.Mode.SRC_ATOP
                    )
                }
                lightSwitch.isChecked = it.get("is_on") as Boolean
            }
        }


        addColorBttn.setOnClickListener {

            ColorPickerDialog
                .Builder(this)                        // Pass Activity Instance
                .setTitle("Pick Color")            // Default "Choose Color"
                .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
                //.setDefaultColor(mDefaultColor)     // Pass Default Color
                .setColorListener { color, colorHex ->
                    // Handle Color Selection
                    current_color = color
                    current_hexcolor = colorHex
                    currentColorSquare.background.setColorFilter(Color.parseColor(current_hexcolor), PorterDuff.Mode.SRC_ATOP)
                    if (email != null) {
                        db.collection("led_panels").document(email).set(
                            hashMapOf("is_on" to panel_is_on, "color_number" to current_color, "hex_color" to current_hexcolor)
                        )
                    }
                }
                .show()
        }

        lightSwitch.setOnClickListener {
            panel_is_on = lightSwitch.isChecked()
            if (email != null) {
                db.collection("led_panels").document(email).set(
                    hashMapOf("is_on" to panel_is_on, "color_number" to current_color, "hex_color" to current_hexcolor)
                )
            }
        }

        checkButton.setOnClickListener {
            if (email != null) {
                db.collection("led_panels").document(email).set(
                    hashMapOf("is_on" to panel_is_on, "color_number" to current_color, "hex_color" to current_hexcolor)
                )
            }
            onBackPressed()
        }
    }
}


