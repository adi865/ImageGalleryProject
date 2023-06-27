package com.example.imagegalleryproject.util

data class DataStatus<out T>(
    val status: Status,
    val data: T? = null,
    val msg: String? = null,
    val isEmpty: Boolean? = false) {

    companion object {
        fun<T> loading(): DataStatus<T> {
            return DataStatus(Status.LOADING)
        }

        fun<T> success(data: T?, isEmpty: Boolean?): DataStatus<T> {
            return DataStatus(Status.SUCCESS, data, isEmpty = isEmpty)
        }

        fun<T> error(error: String): DataStatus<T> {
            return DataStatus(Status.ERROR, msg = error)
        }
    }
}

enum class Status {
    LOADING, ERROR, SUCCESS
}