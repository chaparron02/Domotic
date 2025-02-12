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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class smart_light : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val db_realtime = FirebaseDatabase.getInstance()

    private val panels_reference = db_realtime.getReference("led_panels")

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
        var rgb_color : IntArray
        var panel_is_on = lightSwitch.isChecked()

        //Gather info from intent
        val userInfo : Bundle? = intent.extras
        val email : String? = userInfo?.getString("email")
        val pwd : String? = userInfo?.getString("pwd")
        val usr : String = userInfo?.getString("usr").toString()

        if (email != null) {
//            db.collection("led_panels").document(email).get().addOnSuccessListener {
//                current_hexcolor= (it.get("hex_color") as String?).toString()
//                if (current_hexcolor == "#FFFFF" || current_hexcolor == "#fffff"){
//                    current_hexcolor=current_hexcolor.capitalize()
//                    currentColorSquare.background.setColorFilter(Color.parseColor(current_hexcolor+"F"), PorterDuff.Mode.SRC_ATOP)
//                }else {
//                    currentColorSquare.background.setColorFilter(
//                        Color.parseColor(current_hexcolor),
//                        PorterDuff.Mode.SRC_ATOP
//                    )
//                }
//                lightSwitch.isChecked = it.get("is_on") as Boolean
//            }
            panels_reference.child(usr).get().addOnSuccessListener {
                current_hexcolor = it.child("hex_color").value.toString()
                if (current_hexcolor == "#FFFFF" || current_hexcolor == "#fffff"){
                    current_hexcolor=current_hexcolor.capitalize()
                    currentColorSquare.background.setColorFilter(Color.parseColor(current_hexcolor+"F"), PorterDuff.Mode.SRC_ATOP)
                }else {
                    currentColorSquare.background.setColorFilter(
                        Color.parseColor(current_hexcolor),
                        PorterDuff.Mode.SRC_ATOP
                    )
                }
                lightSwitch.isChecked = it.child("is_on").value as Boolean
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
                    rgb_color= getRgbFromHex(current_hexcolor)
                    currentColorSquare.background.setColorFilter(Color.parseColor(current_hexcolor), PorterDuff.Mode.SRC_ATOP)
                    if (email != null) {
//                        db.collection("led_panels").document(email).update(
//                            mapOf(
//                                "color_number" to current_color,
//                                "hex_color" to current_hexcolor,
//                                "rgb_color" to listOf("r" to rgb_color[0], "g" to rgb_color[1], "b" to rgb_color[2])
//                            )
//                        )
                        panels_reference.child(usr).updateChildren(
                            hashMapOf<String,Any>("color_number" to current_color, "hex_color" to current_hexcolor)
                        )
                        panels_reference.child(usr).child("rgb_color").updateChildren(
                            hashMapOf<String,Any>("r" to rgb_color[0], "g" to rgb_color[1], "b" to rgb_color[2])
                        )
                    }
                }
                .show()
        }

        lightSwitch.setOnClickListener {
            panel_is_on = lightSwitch.isChecked()
            if (email != null) {
//                db.collection("led_panels").document(email).update(
//                    mapOf("is_on" to panel_is_on, "color_number" to current_color, "hex_color" to current_hexcolor)
//                )
                panels_reference.child(usr).updateChildren(
                    hashMapOf<String,Any>("is_on" to panel_is_on,"color_number" to current_color, "hex_color" to current_hexcolor)
                )
            }
        }

        checkButton.setOnClickListener {
            if (email != null) {
//                db.collection("led_panels").document(email).update(
//                    mapOf("is_on" to panel_is_on, "color_number" to current_color, "hex_color" to current_hexcolor)
//                )
                panels_reference.child(usr).updateChildren(
                    hashMapOf<String,Any>("is_on" to panel_is_on,"color_number" to current_color, "hex_color" to current_hexcolor)
                )
            }
            onBackPressed()
        }
    }

    fun getRgbFromHex(hex: String): IntArray {
        val initColor = Color.parseColor(hex)
        val r = Color.red(initColor)
        val g = Color.green(initColor)
        val b = Color.blue(initColor)
        return intArrayOf(r, g, b)
    }
}


