package ru.moviechecker.database.seasons

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.net.URI

@Entity(tableName = "seasons", indices = [Index(value = ["movie_id", "number"], unique = true)])
data class SeasonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "movie_id") val movieId: Int = 0,
    val number: Int,
    var title: String? = null,
    var link: URI? = null,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB) var poster: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SeasonEntity

        if (movieId != other.movieId) return false
        if (number != other.number) return false

        return true
    }

    override fun hashCode(): Int {
        var result = movieId
        result = 31 * result + number
        return result
    }
}
