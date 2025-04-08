package ru.moviechecker.database.sites

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.net.URI

@Dao
interface SiteDao {
    @Query("SELECT * FROM sites s WHERE s.address = :address")
    fun getSiteByAddress(address: URI): SiteEntity?

    @Query("SELECT * FROM sites")
    fun getAllStream(): Flow<List<SiteEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg sites: SiteEntity)
    @Update
    fun update(vararg sites: SiteEntity)
    @Delete
    fun delete(vararg sites: SiteEntity)
}