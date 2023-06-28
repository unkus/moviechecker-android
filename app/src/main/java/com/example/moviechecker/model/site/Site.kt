package com.example.moviechecker.model.site

import android.net.Uri
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.moviechecker.model.Linkable

@Entity(tableName = "site", indices = [Index(value = ["link"], unique = true)])
data class Site(
    override val link: Uri
): Linkable {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}