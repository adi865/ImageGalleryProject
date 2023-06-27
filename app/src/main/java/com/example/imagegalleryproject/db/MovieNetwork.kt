package com.example.imagegalleryproject.db

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class MovieNetwork: Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}