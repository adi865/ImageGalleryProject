package com.example.imagegalleryproject.model

data class Movies(
    val Response: String,
    val Search: List<Search>,
    val totalResults: String
)