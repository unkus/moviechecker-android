package com.example.moviechecker.model.site

import android.net.Uri
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "site", indices = [Index(value = ["link"], unique = true)])
data class Site(
    val link: Uri
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}