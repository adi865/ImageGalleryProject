package com.example.imagegalleryproject.ui.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.imagegalleryproject.databinding.FragmentProfileBinding
import com.example.imagegalleryproject.ui.SignInActivity
import com.example.imagegalleryproject.util.FirebaseUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import java.io.IOException

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth

    private var uri: Uri? = null
    lateinit var mGoogleSignInClient: GoogleSignInClient
    var storage: FirebaseStorage? = null
    private lateinit var storageRef: StorageReference
    private lateinit var databaseRef: DatabaseReference

    private val PICK_IMAGE_REQUEST = 22

    // Uri indicates, where the image will be picked from
    private var filePath: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage!!.reference

        binding.apply {
            FirebaseUtils().firebaseStoreDatabase.collection("accounts").document(mAuth.currentUser!!.uid).get().addOnCompleteListener {
                if(it.isSuccessful) {
                    val result = it.result
                    val username = result.get("name").toString()
                    tvDisplayName.text = username
                    txtEmail.text = mAuth.currentUser!!.email
                }
            }


            profilePic.setOnClickListener {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, "Select Image from here..."),
                    PICK_IMAGE_REQUEST
                )
            }


            if(mAuth != null) {
                val ref: StorageReference = storageRef.child("${mAuth.currentUser!!.uid}/images/")
                ref.downloadUrl.addOnSuccessListener {
                    println("Profile Image Path :${it.toString()}")
                    Picasso.get().load(it.toString()).into(profilePic)
                }
            }

            signOut.setOnClickListener {
                if (mAuth.currentUser != null) {
                    mAuth.signOut()
                    val intent = Intent(requireContext(), SignInActivity::class.java)
                    startActivity(intent)
                } else if (GoogleSignIn.getLastSignedInAccount(requireContext()) != null) {
                    mGoogleSignInClient.signOut().addOnCompleteListener {
                        val intent = Intent(requireContext(), SignInActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    // Override onActivityResult method
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then upload image to firebase storage
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Get the Uri of data
            filePath = data.data
            try {
                // Setting image on image view using Bitmap
                val bitmap =
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, filePath)
                binding.profilePic.setImageBitmap(bitmap)
                val progressDialog = ProgressDialog(requireContext())
                progressDialog.setTitle("Uploading...")
                progressDialog.show()
                val ref: StorageReference = storageRef.child("${mAuth.currentUser!!.uid}/images/")
                ref.putFile(filePath!!).addOnSuccessListener(object:
                    OnSuccessListener<UploadTask.TaskSnapshot?> {
                    override fun onSuccess(p0: UploadTask.TaskSnapshot?) {
                        ref.downloadUrl.addOnSuccessListener(object: OnSuccessListener<Uri> {
                            override fun onSuccess(uri: Uri?) {
                                Picasso.get().load(uri.toString()).into(binding.profilePic)
                            }
                        })
                        // Dismiss dialog
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), "Image Uploaded!!", Toast.LENGTH_SHORT).show()
                    }
                })
                    .addOnFailureListener(OnFailureListener { e -> // Error, Image not uploaded
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), "Failed " + e.message, Toast.LENGTH_SHORT).show()
                    })
                    .addOnProgressListener { taskSnapshot ->
                        // Progress Listener for loading
                        // percentage on the dialog box
                        val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                        progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
                    }
            } catch (e: IOException) {
                // Log the exception
                e.printStackTrace()
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