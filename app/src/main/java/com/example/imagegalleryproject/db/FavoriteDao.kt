package com.example.imagegalleryproject.db

import androidx.room.*
import com.example.imagegalleryproject.model.FavoriteImage
import kotlinx.coroutines.flow.Flow


@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favoriteDao: FavoriteImage)

    @Delete
    suspend fun deleteFavorites(favoriteImage: FavoriteImage)

    @Query("SELECT * FROM favorites")
    fun getFavorites(): Flow<MutableList<FavoriteImage>>

    @Query("SELECT * FROM favorites WHERE favoriteImage LIKE :favoriteImage")
    fun getImage(favoriteImage: String): String
}