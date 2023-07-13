package com.example.database.favorite

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.database.movie.Movie

@Entity(
    tableName = "favorite",
    foreignKeys = [ForeignKey(
        entity = Movie::class,
        parentColumns = ["id"],
        childColumns = ["movie_id"]
    )],
    indices = [Index(value = ["movie_id"], unique = true)]
)
data class Favorite(
    @ColumnInfo(name = "movie_id") val movieId: Int
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
    @ColumnInfo(name = "last_viewed") var lastViewed: Int? = null

    override fun toString(): String {
        return "Favorite(movieId=$movieId)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Favorite

        if (movieId != other.movieId) return false

        return true
    }

    override fun hashCode(): Int {
        return movieId
    }
}