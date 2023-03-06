package com.example.imagegalleryproject.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.imagegalleryproject.model.Image


@Dao
interface ImageDao {

    @Insert
    suspend fun addImage(image: Image)

    @Delete
    suspend fun deleteImage(image: Image)

    @Query("SELECT * from imageTable")
    fun getImages(): LiveData<List<Image>>
}