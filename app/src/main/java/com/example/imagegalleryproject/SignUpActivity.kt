package com.example.imagegalleryproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.imagegalleryproject.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()

        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.btnSignUp.setOnClickListener {
            val email = binding.uName.text.toString()
            val password = binding.password.text.toString()
            val cPassword = binding.cPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && cPassword.isNotEmpty()) {
                if (password == cPassword) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            startActivity(Intent(this, SignInActivity::class.java))
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}