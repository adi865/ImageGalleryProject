package com.example.imagegalleryproject.api

import com.example.imagegalleryproject.model.Movies
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {


    @GET("?apikey=36dffe50")
    suspend fun fetchMoviePosters(
        @Query(value = "s", encoded = true)
        title: String
    ): Response<Movies>
}