package com.example.imagegalleryproject.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "imageTable")
data class Image(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,
    @ColumnInfo(name = "path")
    var path: String,
    @ColumnInfo(name = "dateTaken")
    var dateTaken: String) {

    public class ComparatorByDate(): Comparator<Image> {
        override fun compare(old: Image?, new: Image?): Int {
            return old!!.dateTaken.compareTo(new!!.dateTaken)
        }

    }
}