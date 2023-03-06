package com.example.imagegalleryproject.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.imagegalleryproject.model.Image


@Database(entities = [Image::class], version = 1, exportSchema = false)
abstract class DatabaseInstance: RoomDatabase() {

    abstract fun imageDao(): ImageDao

    companion object {
        @Volatile
        private var INSTANCE: DatabaseInstance? = null

        fun getInstance(context: Context): DatabaseInstance {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        DatabaseInstance::class.java,
                        "imageTable"
                    ).build()
                }
                return instance
            }
        }
    }
}