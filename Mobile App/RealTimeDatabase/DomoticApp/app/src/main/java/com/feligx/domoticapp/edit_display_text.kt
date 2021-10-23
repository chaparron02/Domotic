package com.feligx.domoticapp

import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class edit_display_text : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val db_realtime = FirebaseDatabase.getInstance()

    private val locks_reference = db_realtime.getReference("locks")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_display_text)
        val smart_lock_view = layoutInflater.inflate(R.layout.activity_smart_lock, null)

        val radio_opened = findViewById<RadioGroup>(R.id.radio_button_group)
        val radio_closed = findViewById<RadioGroup>(R.id.radio_button_group_closed)
        val save_button = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.save_texts_button)

        val userInfo : Bundle? = intent.extras
        val email : String? = userInfo?.getString("email")
        val pwd : String? = userInfo?.getString("pwd")
        val usr : String = userInfo?.getString("usr").toString()
        val state : Boolean = userInfo?.getBoolean("state") as Boolean
        var text_open : String = userInfo.getString("text_open").toString()
        var text_closed : String = userInfo.getString("text_closed").toString()


        if (findViewById<RadioButton>(R.id.radio_1).text == text_open){
            radio_opened.check(R.id.radio_1)
        }else if(findViewById<RadioButton>(R.id.radio_2).text == text_open){
            radio_opened.check(R.id.radio_2)
        }else if(findViewById<RadioButton>(R.id.radio_3).text == text_open){
            radio_opened.check(R.id.radio_3)
        }

        if (findViewById<RadioButton>(R.id.radio_closed_1).text == text_closed){
            radio_closed.check(R.id.radio_closed_1)
        }else if(findViewById<RadioButton>(R.id.radio_closed_2).text == text_closed){
            radio_closed.check(R.id.radio_closed_2)
        }else if(findViewById<RadioButton>(R.id.radio_closed_3).text == text_closed){
            radio_closed.check(R.id.radio_closed_3)
        }

        radio_opened.setOnCheckedChangeListener { group, checkedId ->
            val checked = findViewById<RadioButton>(checkedId)
            text_open = checked.text as String
            if (email != null) {
                locks_reference.child(usr).updateChildren(
                    hashMapOf<String,Any>("text_open" to text_open,"text_closed" to text_closed)
                )
            }
        }

        radio_closed.setOnCheckedChangeListener { group, checkedId ->
            val checked_closed = findViewById<RadioButton>(checkedId)
            text_closed = checked_closed.text as String
            if (email != null) {
                db.collection("locks").document(email).update(
                    mapOf("text_open" to text_open, "text_closed" to text_closed)
                )
                locks_reference.child(usr).updateChildren(
                    hashMapOf<String,Any>("text_open" to text_open,"text_closed" to text_closed)
                )
            }
        }

        save_button.setOnClickListener {
            val text = smart_lock_view.findViewById<TextView>(R.id.text_displayed)
            if (state == true){
                text.setText(text_open)
            }else{
                text.setText(text_closed)
            }
            onBackPressed()
        }
    }
}