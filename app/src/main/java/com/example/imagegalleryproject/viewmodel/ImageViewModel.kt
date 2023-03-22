package com.example.imagegalleryproject.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.imagegalleryproject.db.DatabaseInstance
import com.example.imagegalleryproject.db.ImageDao
import com.example.imagegalleryproject.db.PosterRepository
import com.example.imagegalleryproject.model.Movies
import com.example.imagegalleryproject.model.Search
import com.example.imagegalleryproject.util.Resource
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

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
            val response = try {
              repository.getPosters(searchParamter.replace("\\s+","+"))
            } catch(e: IOException) {
                Log.e("My TAG", "No Internet Connection Available ")
               Toast.makeText(context," Can't do search Internet not connected", Toast.LENGTH_SHORT).show()
               return@launch
            } catch(e: HttpException) {
                Log.e("MY TAG", "No valid HTTP response received")
                Toast.makeText(context, "API didn't return a valid response", Toast.LENGTH_SHORT).show()
               return@launch
            }

            if(response.body()!!.Search == null) {
                Toast.makeText(context, "The title you entered not found", Toast.LENGTH_SHORT).show()
                return@launch
            } else {
                imagePathData.postValue(handleResponse(response))
            }
        }
    }

    fun getPostersFromDB() {
        postersFromDB = getImagesFromDB()
    }

    private suspend fun handleResponse(response: Response<Movies>): Resource<Movies> {
        if(response.isSuccessful && response.body()!= null) {
            response.body()?.let {resultResponse ->
                for (search in resultResponse.Search) {
                    insertMovie(search)
                }
                return Resource.Success(resultResponse)
            }
        }
        else {
            Toast.makeText(context, "Failed fetch movie posters because of the error: ${response.message()} or ${response.errorBody()}", Toast.LENGTH_LONG).show()
            return Resource.Error(response.message())
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