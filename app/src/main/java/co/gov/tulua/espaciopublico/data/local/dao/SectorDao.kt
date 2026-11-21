package co.gov.tulua.espaciopublico.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.gov.tulua.espaciopublico.data.local.entity.SectorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SectorDao {

    @Query("SELECT * FROM sectors ORDER BY name ASC")
    fun observeAll(): Flow<List<SectorEntity>>

    @Query("SELECT * FROM sectors ORDER BY name ASC")
    suspend fun getAll(): List<SectorEntity>

    @Query("SELECT * FROM sectors WHERE id = :id")
    suspend fun getById(id: Long): SectorEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(sectors: List<SectorEntity>)

    @Query("DELETE FROM sectors")
    suspend fun deleteAll()
}
