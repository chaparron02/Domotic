package com.feligx.domoticapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
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
                        val Intent = Intent(this, home::class.java).apply{
                            putExtra("email", emailText)
                            putExtra("pwd", passText)
                        }
                        startActivity(Intent)
                    }else{
                    }
                }
            }
        }

        registerBtn.setOnClickListener{
            var emailText = editTextEmail?.getText().toString()
            var passText = passTextEmail?.getText().toString()
            if (emailText.isNotEmpty() && passText.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailText, passText).addOnCompleteListener {
                    if (it.isSuccessful){
                        val Intent = Intent(this, home::class.java).apply{
                            putExtra("email", emailText)
                            putExtra("pwd", passText)
                        }
                        startActivity(Intent)
                    }else{
                    }
                }
            }
        }

    }
}