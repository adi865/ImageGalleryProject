package com.example.imagegalleryproject.model

import com.google.gson.annotations.SerializedName

data class Movies(
    @SerializedName("Response")
    val response: String,
    @SerializedName("Search")
    var search: List<Search>,
    @SerializedName("totalResults")
    val totalResults: String
)