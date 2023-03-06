package com.example.imagegalleryproject.viewmodel

import android.app.Application
import android.content.Context
import android.os.Environment
import androidx.lifecycle.MutableLiveData
import com.example.imagegalleryproject.model.Image
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagegalleryproject.db.ImageDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageViewModel(application: Application, val imageDao: ImageDao): AndroidViewModel(application) {
    var imagePathData = MutableLiveData<List<Image>>()
    private val context = getApplication<Application>().applicationContext

    val images = imageDao.getImages()

    init {
        imagePathData.value = ArrayList()
    }

    fun getImages() {
        viewModelScope.launch {
            val isSDPresent = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

            if (isSDPresent) {
                //the data of images we want in our RecyclerView
                val columns =
                    arrayOf(MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATE_MODIFIED)

                //sort images by their IDs in the gallery, and this sorting variable is passed to query method of contentResolver
                val sortBy = MediaStore.Images.ImageColumns.DATE_ADDED
                var listImages = mutableListOf<Image>()
                withContext(Dispatchers.IO) {
                    //run the query to get images from external storage, with the data and IDs and sorting order as selected
                    //above
                    val cursor = context.contentResolver.query(
                        //source of image
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        //the metadata chosen for fetching
                        columns,
                        null,
                        null,
                        //the attribute by which images will be sorted by default
                        sortBy
                    )

                    val count = cursor!!.count

                    for (i in 0 until count) {
                        cursor.moveToPosition(i)
                        val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                        val dateTaken = cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)
                        var imageId = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                        listImages.add(Image(cursor.getInt(imageId),cursor.getString(dataColumnIndex), cursor.getString(dateTaken)))

                    }
                    cursor.close()
                }
                imagePathData.value = listImages
            }
        }
    }

    fun addImage(image: Image) = viewModelScope.launch {
        Log.i("My TAG", "Inserting Images into favorites from RecyclerView")
        imageDao.addImage(image)
    }
}