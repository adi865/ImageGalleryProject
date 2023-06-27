package com.example.imagegalleryproject.util

data class Resource<out T>(val status: Status, val data: T? = null, val message: String? = null) {

    enum class Status {
        LOADING, SUCCESS, ERROR
    }

    companion object {
        fun <T> loading(): Resource<T> {
            return Resource(Status.LOADING)
        }

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data)
        }

        fun <T> error(error: String): Resource<T> {
            return Resource(Status.ERROR, message = error)
        }
    }


//    class Success<T>(data: T): Resource<T>(data)
//
//    class Error<T>(message: String, data: T? = null): Resource<T>(data, message)
//
//    class Loading<T>: Resource<T>()
}