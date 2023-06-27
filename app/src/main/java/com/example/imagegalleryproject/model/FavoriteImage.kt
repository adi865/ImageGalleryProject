package com.example.imagegalleryproject.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

data class FavoriteImage(
    var favorite: String) {
    constructor(): this("")
}