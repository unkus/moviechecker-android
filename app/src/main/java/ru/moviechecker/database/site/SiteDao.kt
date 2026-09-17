package ru.moviechecker.database.site

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteDao {

    @Query("SELECT * FROM sites s WHERE s.mnemonic = :mnemonic")
    suspend fun getSiteByMnemonic(mnemonic: String): SiteEntity?

    @Query("SELECT * FROM sites")
    fun getAllStream(): Flow<List<SiteEntity>>

    @Query("SELECT * FROM sites s WHERE s.id = :id")
    fun getSiteByIdStream(id: Int): Flow<SiteEntity>

    @Query("SELECT * FROM sites s WHERE s.id = :id")
    suspend fun getSiteById(id: Int): SiteEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg sites: SiteEntity)
    @Update
    suspend fun update(vararg sites: SiteEntity)
    @Delete
    suspend fun delete(vararg sites: SiteEntity)
}