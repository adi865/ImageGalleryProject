package com.example.imagegalleryproject.db

import com.example.imagegalleryproject.api.RetrofitInstance

class PosterRepository() {
    suspend fun getPosters(title: String?) = RetrofitInstance.retrofit.fetchMoviePosters(title!!)

}
