package com.example.imagegalleryproject.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.imagegalleryproject.model.FavoriteImage

@Database(entities = [FavoriteImage::class], version = 1, exportSchema = false)
abstract class FavoriteDatabaseInstance: RoomDatabase() {
    abstract fun imageDao(): FavoriteDao

    companion object{
        @Volatile
        private var INSTANCE: FavoriteDatabaseInstance? = null

        fun getInstance(context: Context): FavoriteDatabaseInstance {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        FavoriteDatabaseInstance::class.java,
                        "favorites"
                    ).allowMainThreadQueries() //allow querying of single row for checking if image being favorited is already in the DB
                        .build()
                }
                return instance
            }
        }
    }
}