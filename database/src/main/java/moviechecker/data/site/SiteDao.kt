package moviechecker.data.site

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.net.URI

@Dao
interface SiteDao {
    @get:Query("SELECT * FROM site")
    val sites: Flow<List<SiteEntity>>

    @Query("SELECT * FROM site s WHERE s.link = :address")
    fun loadSiteByAddress(address: URI): SiteEntity?

    @Insert
    fun insert(site: SiteEntity)

    @Update
    fun update(site: SiteEntity)

    @Delete
    fun delete(site: SiteEntity)
}