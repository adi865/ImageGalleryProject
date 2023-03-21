package com.example.imagegalleryproject.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.imagegalleryproject.model.Image


@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImage(image: Image)

    @Delete
    suspend fun deleteImage(image: Image)

    @Query("SELECT * from imageTable")
    fun getImages(): LiveData<List<Image>>
}