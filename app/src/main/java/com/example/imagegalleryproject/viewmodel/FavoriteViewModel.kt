package com.example.imagegalleryproject.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.imagegalleryproject.db.FavoriteDao
import com.example.imagegalleryproject.db.FavoriteDatabaseInstance
import com.example.imagegalleryproject.model.FavoriteImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sin

class FavoriteViewModel(application: Application, val imageRes: String): AndroidViewModel(application) {
    constructor(application: Application): this(application, "")
    private val context = getApplication<Application>().applicationContext
    val favoriteDao = FavoriteDatabaseInstance.getInstance(context).imageDao()
    var favoriteImages = MediatorLiveData<List<FavoriteImage>>()
    val fetchedImages = favoriteDao.getFavorites()
    val singleImage = MutableLiveData<String>()
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

    fun getSingleFavorite(imageRes: String) = viewModelScope.launch {
       singleImage.value = favoriteDao.getImage(imageRes)
    }
}