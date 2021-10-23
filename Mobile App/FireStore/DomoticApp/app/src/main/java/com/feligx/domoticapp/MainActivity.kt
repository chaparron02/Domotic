package com.feligx.domoticapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginBtn = findViewById<ImageButton>(R.id.loginButton)
        val registerBtn = findViewById<Button>(R.id.registerButton)
        val outlinedTextField = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.outlinedTextField)
        val passTextField = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.outlinedTextFieldPassword)

        var editTextEmail : EditText? = outlinedTextField.getEditText()
        var passTextEmail : EditText? = passTextField.getEditText()

        loginBtn.setOnClickListener{
            var emailText = editTextEmail?.getText().toString()
            var passText = passTextEmail?.getText().toString()
            if (emailText.isNotEmpty() && passText.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    emailText, passText).addOnCompleteListener {
                    if (it.isSuccessful){
                        db.collection("users").document(emailText).get().addOnSuccessListener {
                            var usr= (it.get("username") as String?).toString()
                            val Intent = Intent(this, home::class.java).apply{
                                putExtra("email", emailText)
                                putExtra("pwd", passText)
                                putExtra("username", usr)
                            }
                            startActivity(Intent)
                        }
                    }else{
                    }
                }
            }
        }

        registerBtn.setOnClickListener {
            val Intent = Intent(this, register::class.java)
            startActivity(Intent)
        }

        """registerBtn.setOnClickListener{
            var emailText = editTextEmail?.getText().toString()
            var passText = passTextEmail?.getText().toString()
            if (emailText.isNotEmpty() && passText.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText, passText).addOnCompleteListener {
                    if (it.isSuccessful){
                        val Intent = Intent(this, home::class.java).apply{
                            putExtra("email", emailText)
                            putExtra("pwd", passText)
                            db.collection("led_panels").document(emailText).set(
                                    hashMapOf("is_on" to false, "color_number" to 0, "hex_color" to "#FFFFFF")
                                )
                        }
                        startActivity(Intent)
                    }else{
                    }
                }
            }
        }"""

    }
}