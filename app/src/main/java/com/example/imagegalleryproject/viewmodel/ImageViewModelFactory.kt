package com.example.imagegalleryproject.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.imagegalleryproject.db.ImageDao

class ImageViewModelFactory(val application: Application, val imageDao: ImageDao): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ImageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImageViewModel(application, imageDao) as T
        }
        throw java.lang.IllegalArgumentException("Unknown View Model Class")
    }

}