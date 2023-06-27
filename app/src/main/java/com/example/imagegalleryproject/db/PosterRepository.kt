package com.example.imagegalleryproject.db

import com.example.imagegalleryproject.api.RetrofitInstance
import com.example.imagegalleryproject.model.FavoriteImage
import com.example.imagegalleryproject.model.Search
import com.example.imagegalleryproject.util.DataStatus
import com.example.imagegalleryproject.util.FAILED
import com.example.imagegalleryproject.util.SERVER_ERROR
import com.example.imagegalleryproject.util.SUCCESS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class PosterRepository {
    private val mAuth = FirebaseAuth.getInstance()
    suspend fun getThumbnailsFromApi(title: String?) = flow {
        emit(DataStatus.loading())
        val result = RetrofitInstance.retrofit.fetchMoviePosters(title)
        when (result.code()) {
            SUCCESS -> {
                emit(DataStatus.success(result.body(), false))
            }
            FAILED -> {
                emit(DataStatus.error(result.message()))
            }
            SERVER_ERROR -> {
                emit(DataStatus.error(result.message()))
            }
        }
    }.catch {
        emit(DataStatus.error(it.message.toString()))
    }.flowOn(Dispatchers.IO)


    fun getThumbnails(): Flow<DataStatus<List<Search>>> = callbackFlow {
        val movieList = ArrayList<Search>()
        val currentUser = mAuth.currentUser
        val userId = currentUser!!.uid
        val databaseRef = FirebaseDatabase.getInstance().getReference("movies_list").child(userId)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (responseSnap in snapshot.children) {
                    val search = responseSnap.getValue(Search::class.java)
                    search?.let {
                        movieList.add(it)
                    }
                }
                trySend(DataStatus.success(movieList, false))
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(DataStatus.error(error.message))
            }
        }
        databaseRef.addValueEventListener(valueEventListener)
        awaitClose {
            databaseRef.removeEventListener(valueEventListener)
        }
    }

    fun getFavorites():Flow<DataStatus<List<FavoriteImage>>> = callbackFlow {
        val currentUser = mAuth.currentUser
        val userId = currentUser!!.uid
        val databaseRef = FirebaseDatabase.getInstance().getReference("favorites").child(userId)
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favListImages = mutableListOf<FavoriteImage>()
                if (snapshot.exists()) {
                    for (favSnap in snapshot.children) {
                        val favImages = favSnap.getValue(FavoriteImage::class.java)!!
                        if (!favListImages.contains(favImages)) {
                            favListImages.add(favImages)
                        }
                    }
                    trySend(DataStatus.success(favListImages, false))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(DataStatus.error(error.message))
            }
        }
        databaseRef.addValueEventListener(valueEventListener)

        awaitClose {
            databaseRef.removeEventListener(valueEventListener)
        }
    }
}


