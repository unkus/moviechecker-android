package ru.moviechecker.database.site

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(tableName = "sites", indices = [Index(value = ["mnemonic"], unique = true)])
data class SiteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val mnemonic: String,
    var address: String,
    var title: String?,
    var poster: ByteArray? = null,
    @ColumnInfo(name = "use_mirror", defaultValue = "false")
    var useMirror: Boolean = false,
    var mirror: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SiteEntity

        if (mnemonic != other.mnemonic) return false

        return true
    }

    override fun hashCode(): Int {
        return mnemonic.hashCode()
    }
}