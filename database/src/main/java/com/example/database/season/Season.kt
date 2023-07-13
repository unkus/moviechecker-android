package com.example.database.season

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.database.Linkable
import com.example.database.Numerated
import com.example.database.movie.Movie

@Entity(
    tableName = "season",
    foreignKeys = [ForeignKey(
        entity = Movie::class,
        parentColumns = ["id"],
        childColumns = ["movie_id"],
        onDelete = CASCADE
    )],
    indices = [Index(value = ["movie_id", "number"], unique = true)]
)
data class Season(
    @ColumnInfo(name = "movie_id") val movieId: Int,
    override val number: Int,
    override val link: Uri
): Numerated, Linkable {
    @PrimaryKey(autoGenerate = true) var id: Int = 0

    val title: String
        get() = "$movieId $number"

    override fun toString(): String {
        return "Season(movieId=$movieId, number=$number)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Season

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