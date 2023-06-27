package com.example.imagegalleryproject.viewmodel

import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.imagegalleryproject.screens.Pages
import com.example.imagegalleryproject.util.Event
import com.example.imagegalleryproject.util.FirebaseUtils
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch

class UserInfoViewModel: ViewModel() {
    private val _userInfoDataObserver: MutableLiveData<Task<DocumentSnapshot>> = MutableLiveData()
    val userInforDataObserver: LiveData<Task<DocumentSnapshot>> get() = _userInfoDataObserver

    private val _userProfilePic: MutableLiveData<String> = MutableLiveData()
    val userProfilePic: LiveData<String> get() = _userProfilePic

    private var mAuth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage!!.reference

    @SuppressLint("SuspiciousIndentation")
    fun getUserInfo(navController: NavController) = viewModelScope.launch {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            FirebaseUtils().firebaseStoreDatabase.collection("accounts").document(mAuth.currentUser!!.uid).get().addOnCompleteListener {
                if(it.isSuccessful) {
                    _userInfoDataObserver.postValue(it)
                }
            }
            val ref: StorageReference = storageRef.child("${mAuth.currentUser!!.uid}/images/")
            ref.downloadUrl.addOnSuccessListener(object: OnSuccessListener<Uri> {
                override fun onSuccess(uri: Uri?) {
                    _userProfilePic.postValue(uri!!.toString())
                }
            })
        } else {
            navController.navigate(Pages.SignIn.route)
        }
    }
}