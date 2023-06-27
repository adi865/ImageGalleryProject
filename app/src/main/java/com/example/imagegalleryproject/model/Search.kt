package com.example.imagegalleryproject.model


data class Search(
    var imdbID: String,
    var Poster: String,
    var Title: String ,
    var Type: String,
    var Year: String
){
    constructor(): this("", "", "", "", "")
}
