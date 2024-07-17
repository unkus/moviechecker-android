package ru.moviechecker.database.sites

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.net.URI

@Dao
interface SiteDao {
    @Query("SELECT * FROM sites s WHERE s.address = :address")
    suspend fun getSiteByAddress(address: URI): SiteEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg sites: SiteEntity)
    @Update
    suspend fun update(vararg sites: SiteEntity)
    @Delete
    suspend fun delete(vararg sites: SiteEntity)
}