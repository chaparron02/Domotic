package com.feligx.domoticapp

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.github.dhaval2404.colorpicker.util.setVisibility
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.*
import kotlin.math.roundToInt

class smart_regulator : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_regulator)

        val manual_button = findViewById<Button>(R.id.manual_controls_button)
        val manual_view = findViewById<LinearLayout>(R.id.manual_controls_view)

        val rangeSlider = findViewById<com.google.android.material.slider.RangeSlider>(R.id.slider)
        val temp_text = findViewById<TextView>(R.id.temperature_text)
        val lower_text = findViewById<TextView>(R.id.lower_text)
        val upper_text = findViewById<TextView>(R.id.upper_text)

        val switch_ac = findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.switch_ac)
        val switch_radiator = findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.switch_radiator)
        var ac_is_on: Boolean
        var radiator_is_on: Boolean

        val save_button = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.save_button)

        val userInfo : Bundle? = intent.extras
        val email : String = userInfo?.getString("email").toString()
        val pwd : String? = userInfo?.getString("pwd")

        val listener = listenMessages(email, temp_text)

        db.collection("regulators").document(email).get().addOnSuccessListener {
            ac_is_on= (it.get("ac_on") as Boolean)
            radiator_is_on= (it.get("radiator_on") as Boolean)
            val min = (it.get("lower_limit").toString().toFloat())
            val max = (it.get("upper_limit").toString().toFloat())
            rangeSlider.values = listOf(min ,max)
            switch_ac.isChecked = ac_is_on
            switch_radiator.isChecked = radiator_is_on
            temp_text.text = (it.get("actual_temperature").toString())+"°"
            upper_text.text = max.roundToInt().toString()
            lower_text.text = min.roundToInt().toString()
        }

        rangeSlider.addOnChangeListener { rangeSlider, value, fromUser ->
            // Responds to when slider's value is changed
            val vals = rangeSlider.values
            val min: Float = Collections.min(vals)
            val max: Float = Collections.max(vals)
            upper_text.text = max.toString()
            lower_text.text = min.toString()
            db.collection("regulators").document(email).update(
                mapOf("upper_limit" to max, "lower_limit" to min)
            )
        }

        manual_button.setOnClickListener {
            if (manual_view.isVisible){
                manual_view.setVisibility(false)
            }else{
                manual_view.setVisibility(true)
            }
        }

        switch_ac.setOnClickListener {
            ac_is_on = switch_ac.isChecked()
            db.collection("regulators").document(email).update(
                mapOf("ac_on" to ac_is_on)
            )
        }
        switch_radiator.setOnClickListener {
            radiator_is_on = switch_radiator.isChecked()
            db.collection("regulators").document(email).update(
                mapOf("radiator_on" to radiator_is_on)
            )
        }

        save_button.setOnClickListener {
            listener.remove()
            onBackPressed()
        }
    }

    private fun listenMessages(email:String, temp_view:TextView): ListenerRegistration {
        val listener = db.collection("regulators").document(email)
            .addSnapshotListener { documentSnapshot, error ->
                if (error != null) {
                    //Manage error
                } else if (documentSnapshot != null) {
                    temp_view.text =
                        documentSnapshot.data?.get("actual_temperature").toString() + "°"
                }
            }
        return listener
    }
}
