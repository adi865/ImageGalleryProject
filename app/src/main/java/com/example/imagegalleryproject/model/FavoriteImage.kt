package com.example.imagegalleryproject.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favorites")
data class FavoriteImage(
    @PrimaryKey
    @ColumnInfo(name = "favoriteImage")
    val favorite: String) {
}