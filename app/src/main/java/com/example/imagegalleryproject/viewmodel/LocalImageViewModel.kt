package com.example.imagegalleryproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.imagegalleryproject.db.DatabaseInstance
import com.example.imagegalleryproject.db.PosterRepository

class LocalImageViewModel(application: Application, val repository: PosterRepository): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val imageDao = DatabaseInstance.getInstance(context).imageDao()
    var localDBImages = fetchImages()


    private fun fetchImages() = imageDao.getImages()
}