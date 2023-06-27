package com.example.imagegalleryproject.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.example.imagegalleryproject.db.PosterRepository
import com.example.imagegalleryproject.model.Movies
import com.example.imagegalleryproject.model.Search
import com.example.imagegalleryproject.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.*
import kotlinx.coroutines.launch
import java.util.*


class ImageViewModel(
    application: Application,
    val repository: PosterRepository,
    val searchParamter: String?
) : AndroidViewModel(application) {
    constructor(application: Application, repository: PosterRepository) : this(
        application, repository, ""
    )

    private val context = getApplication<Application>().applicationContext

    var imagePathData = MutableLiveData<Resource<Movies>>()
    var movieData =
        MutableLiveData<List<Search>>() //for online fetching, the first when request is made to API
    var searchResult = MutableLiveData<List<Search>>()
    private var tempList = ArrayList<Search>()
    private val mAuth = FirebaseAuth.getInstance()

    private lateinit var databaseRef: DatabaseReference


    init {
//        if (isConnectionAvailable(context)) {
//            getImages(searchParamter)
//
//        } else {
//            getDataFromCloud()
//        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun getImages(searchParamter: String?) {
        viewModelScope.launch {
//            imagePathData.postValue(Resource.Loading())
//            val response = try {
//                repository.getPosters(searchParamter!!.replace("\\s+", "+"))
//            } catch (e: IOException) {
//                Log.e("My TAG", "No Internet Connection Available ")
//                Toast.makeText(
//                    context, " Can't do search Internet not connected", Toast.LENGTH_SHORT
//                ).show()
//                return@launch
//            } catch (e: HttpException) {
//                Log.e("MY TAG", "No valid HTTP response received")
//                Toast.makeText(context, "API didn't return a valid response", Toast.LENGTH_SHORT)
//                    .show()
//                return@launch
//            }
//            if (response.body()!!.Search == null) {
//                Toast.makeText(context, "The title you entered not found", Toast.LENGTH_SHORT)
//                    .show()
//                return@launch
//            } else {
//                tempList.addAll(response.body()!!.Search)
//                val databaseRef = FirebaseDatabase.getInstance().getReference("movies_list")
//                val flow = callbackFlow<List<Search>> {
//                    trySend(tempList).isSuccess
//                    awaitClose {
//                    }
//                }
//                flow.collect { updatedList ->
//                    searchResult.postValue(updatedList)
//                }
//                databaseRef.child(mAuth.currentUser!!.uid).setValue(response.body()!!.Search)
//                    .addOnSuccessListener {
////                                Toast.makeText(context, "Data Added Successfully", Toast.LENGTH_SHORT).show()
//                    }.addOnFailureListener {
//                        Toast.makeText(context, "Failed to add Data", Toast.LENGTH_SHORT).show()
//                    }
//                imagePathData.postValue(handleResponse(response))
//            }
//        }
        }

//    private fun handleResponse(response: Response<Movies>): Resource<Movies> {
//        if (response.isSuccessful && response.body() != null) {
//            response.body()?.let { resultResponse ->
//                resultResponse.Search.forEach { _ ->
////                    insertMovie(it)
//                }
//                return Resource.Error(response.message())
//            }
//        }
//        return Resource.Error(response.message())
//    }

//    private fun isConnectionAvailable(context: Context): Boolean {
//        val connectivityManager: ConnectivityManager =
//            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        return connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnected
//    }

        fun getDataFromCloud() {
            val databaseRef = FirebaseDatabase.getInstance().getReference("movies_list")
                .child(mAuth.currentUser!!.uid)
            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val movieList = ArrayList<Search>()
                    for (responseSnap in snapshot.children) {
                        val search = responseSnap.getValue(Search::class.java)
                        search?.let {
                            movieList.add(it)
                        }
                    }
                    if (movieList.isNotEmpty()) {
                        movieData.postValue(movieList)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}