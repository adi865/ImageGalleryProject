package com.example.imagegalleryproject.model

data class Image(var path: String, var dateTaken: String) {

    public class ComparatorByDate(): Comparator<Image> {
        override fun compare(old: Image?, new: Image?): Int {
            return old!!.dateTaken.compareTo(new!!.dateTaken)
        }

    }
}