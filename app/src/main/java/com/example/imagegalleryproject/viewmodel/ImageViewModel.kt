package com.example.imagegalleryproject.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.*
import com.example.imagegalleryproject.db.PosterRepository
import com.example.imagegalleryproject.model.Movies
import com.example.imagegalleryproject.model.Search
import com.example.imagegalleryproject.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class ImageViewModel(application: Application, val repository: PosterRepository, val searchParameter: String): AndroidViewModel(application) {
    var imagePathData = MediatorLiveData<Resource<Movies>>()
    private val context = getApplication<Application>().applicationContext
    var postersFromDB = repository.getImagesFromDB()

    init {
        if(isConnectionAvailable(context)) {
            getImages(searchParameter)
        } else {
            getPostersFromDB()
        }

    }


    @SuppressLint("SuspiciousIndentation")
    fun getImages(searchParamter: String) {
        viewModelScope.launch {
            imagePathData.postValue(Resource.Loading())
            val response = repository.getPosters(searchParamter.replace("\\s+","+"))
            imagePathData.postValue(handleResponse(response))
        }
    }

    fun getPostersFromDB() {
        postersFromDB = repository.getImagesFromDB()
    }

    private suspend fun handleResponse(response: Response<Movies>): Resource<Movies> {
        if(response.isSuccessful) {
            response.body()?.let {resultResponse ->
                for (search in resultResponse.Search) {
                    repository.insertMovie(search)
                }
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun isConnectionAvailable(context: Context): Boolean {
        val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo()!!.isConnected
    }
}