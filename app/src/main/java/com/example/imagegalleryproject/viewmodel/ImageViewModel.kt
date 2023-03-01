package com.example.imagegalleryproject.viewmodel

import android.app.Application
import android.content.Context
import android.os.Environment
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.example.imagegalleryproject.model.Image
import android.os.Handler
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class ImageViewModel(application: Application) : AndroidViewModel(application) {
    private var imagePathData = MutableLiveData<Image>()
    private val delay = 2000L
    private val context = getApplication<Application>().applicationContext

    init {
        getImages()
    }


    fun getModelImage(): LiveData<Image> = imagePathData


    fun getImages() {
        Handler(Looper.getMainLooper()).postDelayed({
            updateImagePath(context)
        }, delay)
    }

    fun updateImagePath(context: Context) {
        val isSDPresent = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

        if (isSDPresent) {
            //the data of images we want in our RecyclerView
            val columns =
                arrayOf(MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns._ID)

            //sort images by their IDs in the gallery, and this sorting variable is passed to query method of contentResolver
            val sortBy = MediaStore.Images.ImageColumns.DATE_TAKEN

            //run the query to get images from external storage, with the data and IDs and sorting order as selected
            //above
            val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns,
                null,
                null,
                sortBy
            )

            val count = cursor!!.count

            for (i in 0 until count) {
                cursor.moveToPosition(i)
                val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                imagePathData.value?.path  = cursor.getString(dataColumnIndex)//returns the path of the image
            }
            cursor.close()
        }
    }
}