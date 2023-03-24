package com.example.imagegalleryproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewerViewModel(application: Application): AndroidViewModel(application){
    val name = MutableLiveData<String>()

    fun fetchImage(imageRes: String) {
        name.value = imageRes
    }
}