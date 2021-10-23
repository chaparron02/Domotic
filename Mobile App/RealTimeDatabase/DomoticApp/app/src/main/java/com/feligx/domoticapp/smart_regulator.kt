package com.feligx.domoticapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.github.dhaval2404.colorpicker.util.setVisibility
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.math.roundToInt

class smart_regulator : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val db_realtime = FirebaseDatabase.getInstance()
    private val regulator_reference = db_realtime.getReference("regulators")

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
        val usr : String =userInfo?.getString("usr").toString()

        regulator_reference.child(usr).get().addOnSuccessListener {
            ac_is_on= it.child("ac_on").value as Boolean
            radiator_is_on= it.child("radiator_on").value as Boolean
            val min= it.child("lower_limit").value.toString().toFloat()
            val max= it.child("upper_limit").value.toString().toFloat()
            rangeSlider.values = listOf(min ,max)
            switch_ac.isChecked = ac_is_on
            switch_radiator.isChecked = radiator_is_on
            temp_text.text = (it.child("actual_temperature").value.toString())+"°"
            upper_text.text = max.roundToInt().toString()
            lower_text.text = min.roundToInt().toString()
        }

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.i("firebase", "Got value ${dataSnapshot.value}")
                val data = dataSnapshot.value as Map<*, *>
                val usrDbData = data[usr] as Map<*,*>
                ac_is_on = usrDbData["ac_on"] as Boolean
                radiator_is_on = usrDbData["radiator_on"] as Boolean
//                val min = usrDbData["lower_limit"].toString().toFloat()
//                val max = usrDbData["upper_limit"].toString().toFloat()
//                rangeSlider.values = listOf(min ,max)
//                switch_ac.isChecked = ac_is_on
//                switch_radiator.isChecked = radiator_is_on
                temp_text.text = (usrDbData["actual_temperature"].toString())+"°"
//                upper_text.text = max.roundToInt().toString()
//                lower_text.text = min.roundToInt().toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }

        regulator_reference.addValueEventListener(postListener)

        rangeSlider.addOnChangeListener { rangeSlider, value, fromUser ->
            // Responds to when slider's value is changed
            val vals = rangeSlider.values
            val min: Float = Collections.min(vals)
            val max: Float = Collections.max(vals)
            upper_text.text = max.toString()
            lower_text.text = min.toString()
            regulator_reference.child(usr).updateChildren(
                    hashMapOf<String,Any>("upper_limit" to max, "lower_limit" to min)
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
            regulator_reference.child(usr).updateChildren(
                hashMapOf<String,Any>("ac_on" to ac_is_on)
            )
        }
        switch_radiator.setOnClickListener {
            radiator_is_on = switch_radiator.isChecked()
            regulator_reference.child(usr).updateChildren(
                hashMapOf<String,Any>("radiator_on" to radiator_is_on)
            )
        }

        save_button.setOnClickListener {
//            listener.remove()
            regulator_reference.removeEventListener(postListener)
            onBackPressed()
        }
    }
}
