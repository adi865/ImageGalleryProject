package com.example.imagegalleryproject.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.imagegalleryproject.db.PosterRepository

class ImageViewModelFactory(val application: Application, val posterRepostory:PosterRepository, val searchParameter: String): ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ImageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImageViewModel(application, posterRepostory, searchParameter) as T
        }
        throw java.lang.IllegalArgumentException("Unknown View Model Class")
    }

}