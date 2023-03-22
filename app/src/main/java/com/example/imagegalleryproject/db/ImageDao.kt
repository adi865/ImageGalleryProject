package com.example.imagegalleryproject.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.imagegalleryproject.model.Search


@Dao
interface ImageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImage(search: Search)

    @Query("DELETE from imageTable")
    suspend fun deleteImage()

    @Query("SELECT * from imageTable")
    fun getImages(): LiveData<List<Search>>
}