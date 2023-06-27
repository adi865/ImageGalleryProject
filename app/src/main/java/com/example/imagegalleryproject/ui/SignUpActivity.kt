package com.example.imagegalleryproject.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.imagegalleryproject.databinding.ActivitySignUpBinding
import com.example.imagegalleryproject.model.User
import com.example.imagegalleryproject.util.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        binding.tvSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        binding.btnSignUp.setOnClickListener {
            val displayName = binding.displayName.text.toString()
            val email = binding.uName.text.toString()
            val password = binding.password.text.toString()
            val cPassword = binding.cPassword.text.toString()



            if (displayName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && cPassword.isNotEmpty()) {
                if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (password == cPassword) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                            if (it.isSuccessful) {
                                val userId = it.result.user!!.uid
                                val account = {userId}
                                val user = User(displayName, email, password)
                                FirebaseUtils().firebaseStoreDatabase.collection("accounts").document(userId).set(account)
                                FirebaseUtils().firebaseStoreDatabase.collection("accounts").document(userId).set(user)
                                startActivity(Intent(this, SignInActivity::class.java))
                            } else {
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "Email entered in wrong format", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fields can't be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}