package com.example.imagegalleryproject.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.example.imagegalleryproject.db.PosterRepository
import com.example.imagegalleryproject.model.FavoriteImage
import com.example.imagegalleryproject.util.DataStatus
import com.example.imagegalleryproject.util.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class FavoriteViewModel: ViewModel() {
    private val repository = PosterRepository()

    val fetchedImages = MutableLiveData<DataStatus<List<FavoriteImage>>>()

    private lateinit var databaseRef: DatabaseReference

    private val mAuth = FirebaseAuth.getInstance()

    private val statusMessage = MutableLiveData<Event<String>>()

    val message : LiveData<Event<String>>
        get() = statusMessage

    fun addFavorites(favoriteImage: java.util.ArrayList<FavoriteImage>) = viewModelScope.launch {
        databaseRef =
            FirebaseDatabase.getInstance().getReference("favorites").child(mAuth.currentUser!!.uid)
        var mapOfImages = LinkedHashMap<String, Any>()

        favoriteImage.forEachIndexed { index, favoriteImage ->
            mapOfImages[index.toString()] = favoriteImage.favorite
        }
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val existingData = snapshot.value as? List<*>
                val updatedData = existingData?.plus(favoriteImage) ?: favoriteImage
                databaseRef.setValue(updatedData)
                    .addOnSuccessListener {
                            statusMessage.value = Event("Images Added to Favorites")
                    }
                    .addOnFailureListener {
                            statusMessage.value = Event("Failed to Add Images to Favorites")
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                            statusMessage.value = Event("Adding Favorites cancelled")
            }
        })
    }

    fun addSingleFavorite(selectedImage: ArrayList<FavoriteImage>) = viewModelScope.launch {
        databaseRef =
            FirebaseDatabase.getInstance().getReference("favorites").child(mAuth.currentUser!!.uid)
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val existingData = snapshot.value as? List<*>
                val updatedData = existingData?.plus(selectedImage) ?: selectedImage
                databaseRef.setValue(updatedData)
                    .addOnCompleteListener {
                            statusMessage.value = Event("Image added to favorites")
                    }.addOnFailureListener {
                            statusMessage.value = Event("Failed to add the image to favorites")
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                statusMessage.value = Event("Cancelled adding image to favorites")
            }
        })
    }

    fun deleteFavorites(favoriteImage: ArrayList<FavoriteImage>) = viewModelScope.launch {
        val databaseRef = FirebaseDatabase.getInstance().getReference("favorites").child(mAuth.currentUser!!.uid)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val existingFavorites =
                    snapshot.children.map { it.getValue(FavoriteImage::class.java) }
                val updatedFavorites =
                    existingFavorites.filter { !favoriteImage.contains(it) }

                databaseRef.setValue(updatedFavorites)
                    .addOnSuccessListener {
                        statusMessage.value = Event("Deleted the selected images")
                    }
                    .addOnFailureListener {
                        statusMessage.value = Event("Failed to delete the selected images")
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                        statusMessage.value = Event("Cancelled the deletion of selected images")
            }
        })
    }

    fun getAllFavorites() = viewModelScope.launch {
        repository.getFavorites().collect {favorites ->
            fetchedImages.value = favorites
        }
    }
}