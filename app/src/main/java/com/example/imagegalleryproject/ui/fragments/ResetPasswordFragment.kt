package com.example.imagegalleryproject.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast

import androidx.fragment.app.DialogFragment
import com.example.imagegalleryproject.databinding.FragmentResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.ComposeView

class ResetPasswordFragment : DialogFragment() {
    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        _binding = FragmentResetPasswordBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        binding.apply {
            btnConfirm.setOnClickListener {
                val email = emailText.text.toString().trim()
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // if isSuccessful then done message will be shown
                        // and you can change the password
                        Toast.makeText(it.context, "Email Sent", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(it.context, "Not a valid email", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(it.context, "Email doesn't exist", Toast.LENGTH_SHORT).show()
                }
                dismiss()
            }
            imgClose.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}