package com.example.imagegalleryproject.db

import com.example.imagegalleryproject.api.RetrofitInstance
import com.example.imagegalleryproject.model.Search

class PosterRepository(private val db: DatabaseInstance) {
    private val imageDao = db.imageDao()
    suspend fun getPosters(title: String) = RetrofitInstance.retrofit.fetchMoviePosters(title)


    suspend fun insertMovie(search: Search) = imageDao.addImage(search)


    suspend fun removeImage(search:Search) = imageDao.deleteImage(search)

    fun getImagesFromDB() = imageDao.getImages()
}
