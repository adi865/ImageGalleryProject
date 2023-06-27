package com.example.imagegalleryproject.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.imagegalleryproject.model.Search
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class LocalImageViewModel(application1: Application): AndroidViewModel(application1) {

    var localMovieList: MutableLiveData<List<Search>> = MutableLiveData()

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    private val mAuth = FirebaseAuth.getInstance()


    private lateinit var databaseRef: DatabaseReference

   fun getMovieDataforOffline() = viewModelScope.launch {
        val movieList = mutableListOf<Search>()
        val databaseRef = FirebaseDatabase.getInstance().getReference("movies_list").child(mAuth.currentUser!!.uid)
        val flow = callbackFlow<List<Search>> {
            val valueEventListener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (responseSnap in snapshot.children) {
                        val search = responseSnap.getValue(Search::class.java)
                        search?.let {
                            movieList.add(it)
                        }
                    }
                    trySend(movieList).isSuccess
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            databaseRef.addValueEventListener(valueEventListener)

            awaitClose {
                databaseRef.removeEventListener(valueEventListener)
            }
        }

        flow.collect {updatedList ->
            localMovieList.postValue(updatedList)
        }
    }
}