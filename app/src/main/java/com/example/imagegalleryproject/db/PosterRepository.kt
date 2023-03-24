package com.example.imagegalleryproject.db

import com.example.imagegalleryproject.api.RetrofitInstance
import com.example.imagegalleryproject.model.Search

class PosterRepository() {
    suspend fun getPosters(title: String?) = RetrofitInstance.retrofit.fetchMoviePosters(title!!)

}
