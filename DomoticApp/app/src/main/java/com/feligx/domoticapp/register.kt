package com.feligx.domoticapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class register : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val registerBtn = findViewById<ImageButton>(R.id.registerBtn)
        val usernameTextField = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.usernameTextField)
        val outlinedTextField = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.outlinedTextField)
        val passTextField = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.outlinedTextFieldPassword)


        var editTextEmail : EditText? = outlinedTextField.getEditText()
        var passTextEmail : EditText? = passTextField.getEditText()
        var usrTextEmail : EditText? = usernameTextField.getEditText()

        registerBtn.setOnClickListener{
            var emailText = editTextEmail?.getText().toString()
            var passText = passTextEmail?.getText().toString()
            var usrText = usrTextEmail?.getText().toString()
            if (emailText.isNotEmpty() && passText.isNotEmpty() && usrText.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText, passText).addOnCompleteListener {
                    if (it.isSuccessful){
                        val Intent = Intent(this, home::class.java).apply{
                            putExtra("email", emailText)
                            putExtra("pwd", passText)
                            putExtra("username", usrText)

                            db.collection("users").document(emailText).set(
                                hashMapOf("email" to emailText, "password" to passText, "username" to usrText)
                            )
                            db.collection("led_panels").document(emailText).set(
                                hashMapOf("is_on" to false, "color_number" to 0, "hex_color" to "#FFFFFF")
                            )
                            db.collection("locks").document(emailText).set(
                                hashMapOf("is_locked" to false, "text_open" to "It's open!", "text_closed" to "It's closed!")
                            )
                        }
                        startActivity(Intent)
                        finish()
                    }else{
                    }
                }
            }
        }

    }
}