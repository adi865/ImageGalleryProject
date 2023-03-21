package com.example.imagegalleryproject.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.imagegalleryproject.model.FavoriteImage

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favoriteDao: FavoriteImage)


    @Delete
    suspend fun deleteFavorites(favoriteImage: FavoriteImage)

    @Query("SELECT * FROM favorites")
    fun getFavorites(): LiveData<List<FavoriteImage>>
}