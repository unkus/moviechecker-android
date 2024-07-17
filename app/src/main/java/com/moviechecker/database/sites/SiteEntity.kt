package com.moviechecker.database.sites

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.net.URI

@Entity(tableName = "sites", indices = [Index(value = ["address"], unique = true)])
data class SiteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val address: URI
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SiteEntity

        if (address != other.address) return false

        return true
    }

    override fun hashCode(): Int {
        return address.hashCode()
    }
}