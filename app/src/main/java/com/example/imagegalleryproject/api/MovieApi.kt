package com.example.imagegalleryproject.api

import com.example.imagegalleryproject.api.RetrofitInstance.Companion.API_KEY
import com.example.imagegalleryproject.model.Movies
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {

    @GET("/")
    suspend fun fetchMoviePosters(
        @Query(value = "s", encoded = true)
        title: String?,
        @Query("apikey")
        apiKey: String = API_KEY
    ): Response<Movies>
}