package com.example.imagegalleryproject.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.*
import com.example.imagegalleryproject.db.DatabaseInstance
import com.example.imagegalleryproject.db.ImageDao
import com.example.imagegalleryproject.db.PosterRepository
import com.example.imagegalleryproject.model.Movies
import com.example.imagegalleryproject.model.Search
import com.example.imagegalleryproject.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class ImageViewModel(application: Application, val repository: PosterRepository, val searchParameter: String): AndroidViewModel(application) {
    var imagePathData = MediatorLiveData<Resource<Movies>>()
    private val context = getApplication<Application>().applicationContext
    private val imageDao = DatabaseInstance.getInstance(context).imageDao()
    var postersFromDB = getImagesFromDB()

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
        postersFromDB = getImagesFromDB()
    }

    private suspend fun handleResponse(response: Response<Movies>): Resource<Movies> {
        if(response.isSuccessful) {
            response.body()?.let {resultResponse ->
                for (search in resultResponse.Search) {
                    insertMovie(search)
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

    suspend fun insertMovie(search: Search) = imageDao.addImage(search)

    suspend fun removeImage(search:Search) = imageDao.deleteImage(search)


    fun getImagesFromDB() = imageDao.getImages()
}