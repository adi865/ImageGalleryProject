package com.example.imagegalleryproject.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "imageTable")
data class Image(
    @PrimaryKey
    @ColumnInfo(name = "path")
    var path: String, )