package com.feligx.domoticapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val db_realtime = FirebaseDatabase.getInstance()

    private val users_reference = db_realtime.getReference("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginBtn = findViewById<ImageButton>(R.id.loginButton)
        val registerBtn = findViewById<Button>(R.id.registerButton)
        val outlinedTextField = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.outlinedTextField)
        val passTextField = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.outlinedTextFieldPassword)

        var editTextUsr : EditText? = outlinedTextField.getEditText()
        var passTextEmail : EditText? = passTextField.getEditText()

        loginBtn.setOnClickListener{
            var usrText = editTextUsr?.getText().toString()
            var passText = passTextEmail?.getText().toString()
            if (usrText.isNotEmpty() && passText.isNotEmpty()) {
                users_reference.child(usrText).get().addOnSuccessListener {
                    val email= it.child("email").value.toString()
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(
                        email, passText).addOnCompleteListener {
                        if (it.isSuccessful){
                            val Intent = Intent(this, home::class.java).apply{
                                putExtra("email", email)
                                putExtra("pwd", passText)
                                putExtra("username", usrText)
                            }
                            startActivity(Intent)
//                        }
                        }else{
                        }
                    }
                }
            }
        }

        registerBtn.setOnClickListener {
            val Intent = Intent(this, register::class.java)
            startActivity(Intent)
        }
    }
}