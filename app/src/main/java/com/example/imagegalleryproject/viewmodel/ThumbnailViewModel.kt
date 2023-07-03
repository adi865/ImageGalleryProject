package com.example.imagegalleryproject.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.example.imagegalleryproject.db.PosterRepository
import com.example.imagegalleryproject.ui.pages.SearchWidgetState
import com.example.imagegalleryproject.model.Movies
import com.example.imagegalleryproject.model.Search
import com.example.imagegalleryproject.util.DataStatus
import com.example.imagegalleryproject.util.Event
import com.example.imagegalleryproject.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class ThumbnailViewModel : ViewModel() {
    private val _searchWidgetState: MutableState<SearchWidgetState> =
        mutableStateOf(value = SearchWidgetState.CLOSED)
    private val repository = PosterRepository()
    val searchWidgetState: State<SearchWidgetState> = _searchWidgetState

    private val _searchTextState: MutableState<String> = mutableStateOf("")
    val searchTextState: State<String> = _searchTextState

    var movieData = MutableLiveData<DataStatus<List<Search>>>()

    var apiResult = MutableLiveData<DataStatus<Movies>>()

    private val mAuth = FirebaseAuth.getInstance()


    private val _stateImagePathData = MutableLiveData<Resource<Movies>>()
    val stateImagePathData: LiveData<Resource<Movies>> get() = _stateImagePathData

    fun updateSearchWidgetState(newValue: SearchWidgetState) {
        _searchWidgetState.value = newValue
    }

    fun updateSearchTextState(newValue: String) {
        _searchTextState.value = newValue
    }

    private val statusMessage = MutableLiveData<Event<String>>()

    val message : LiveData<Event<String>>
        get() = statusMessage

    fun getImages(searchParamter: String?) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("movies_list")
        viewModelScope.launch {
            repository.getThumbnailsFromApi(searchParamter).collect {
                apiResult.value = it
            }

            apiResult.value?.let { resource ->
                resource.data?.let { postersList ->
                    postersList.search.let {
                        val newRef = databaseRef.child(mAuth.currentUser!!.uid).setValue(it)
                        newRef.addOnSuccessListener {
                            statusMessage.value = Event("Data Added Successfully")

                        }
                        newRef.addOnFailureListener {
                            statusMessage.value = Event("Failed to Add Data Successfully")
                        }
                    }
                }
            }
        }
    }


    fun getDataFromCloud() = viewModelScope.launch {
        repository.getThumbnails().collect { resource ->
              movieData.value = resource
        }
    }
}