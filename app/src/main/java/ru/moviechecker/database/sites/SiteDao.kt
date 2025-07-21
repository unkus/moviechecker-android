package ru.moviechecker.database.sites

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteDao {

    @Query("SELECT * FROM sites s WHERE s.mnemonic = :mnemonic")
    fun getSiteByMnemonic(mnemonic: String): SiteEntity?

    @Query("SELECT * FROM sites")
    fun getAllStream(): Flow<List<SiteEntity>>

    @Query("SELECT * FROM sites s WHERE s.id = :id")
    fun getSiteByIdStream(id: Int): Flow<SiteEntity>

    @Query("SELECT * FROM sites s WHERE s.id = :id")
    fun getSiteById(id: Int): SiteEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg sites: SiteEntity)
    @Update
    fun update(vararg sites: SiteEntity)
    @Delete
    fun delete(vararg sites: SiteEntity)
}