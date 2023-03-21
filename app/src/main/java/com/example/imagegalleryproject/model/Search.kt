package com.example.imagegalleryproject.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "imageTable")
data class Search(
    @PrimaryKey
    val imdbID: String,
    @ColumnInfo(name = "poster")
    val Poster: String,
    @ColumnInfo(name="title")
    val Title: String,
    @ColumnInfo(name="type")
    val Type: String,
    @ColumnInfo(name="year")
    val Year: String
)