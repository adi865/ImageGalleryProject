package com.example.imagegalleryproject.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.imagegalleryproject.db.FavoriteDao
import com.example.imagegalleryproject.db.ImageDao

class FavoriteViewModelFactory(val application: Application, val favoriteDao: FavoriteDao): ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteViewModel(application, favoriteDao) as T
        }
        throw java.lang.IllegalArgumentException("Unknown View Model Class")
    }
}