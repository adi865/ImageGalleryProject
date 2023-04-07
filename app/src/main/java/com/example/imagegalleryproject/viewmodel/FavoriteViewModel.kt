package com.example.imagegalleryproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.example.imagegalleryproject.db.FavoriteDao
import com.example.imagegalleryproject.db.FavoriteDatabaseInstance
import com.example.imagegalleryproject.model.FavoriteImage
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    val favoriteDao = FavoriteDatabaseInstance.getInstance(context).imageDao()

    var favoriteImages = MediatorLiveData<List<FavoriteImage>>()
    val fetchedImages = favoriteDao.getFavorites()

    init {
        favoriteImages.value = ArrayList()
    }

    fun getFavorites() {
        viewModelScope.launch {
            favoriteImages.addSource(fetchedImages) {
                favoriteImages.value = it
            }
        }
    }

    fun addFavorites(favoriteImage: FavoriteImage) = viewModelScope.launch {
        favoriteDao.addFavorite(favoriteImage)
    }

    fun deleteFavorites(favoriteImage: FavoriteImage) = viewModelScope.launch {
        favoriteDao.deleteFavorites(favoriteImage)
    }
}