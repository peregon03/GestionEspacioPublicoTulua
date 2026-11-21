package co.gov.tulua.espaciopublico.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import co.gov.tulua.espaciopublico.data.local.entity.ModuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModuleDao {

    @Query("SELECT * FROM modules ORDER BY code ASC")
    fun observeAll(): Flow<List<ModuleEntity>>

    @Query(
        """
        SELECT * FROM modules
        WHERE (:search IS NULL OR name LIKE '%' || :search || '%' OR code LIKE '%' || :search || '%' OR vendorFullName LIKE '%' || :search || '%' OR vendorIdNumber LIKE '%' || :search || '%')
        AND (:type IS NULL OR type = :type)
        AND (:status IS NULL OR status = :status)
        AND (:sectorId IS NULL OR sectorId = :sectorId)
        ORDER BY code ASC
        LIMIT :size OFFSET (:page - 1) * :size
        """
    )
    suspend fun getModules(
        search: String?,
        type: String?,
        status: String?,
        sectorId: Long?,
        page: Int,
        size: Int,
    ): List<ModuleEntity>

    @Query("SELECT * FROM modules WHERE id = :id")
    suspend fun getById(id: Long): ModuleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(modules: List<ModuleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(module: ModuleEntity)

    @Update
    suspend fun update(module: ModuleEntity)

    @Query("DELETE FROM modules")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM modules")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM modules WHERE type = :type")
    suspend fun countByType(type: String): Int

    @Query("SELECT COUNT(*) FROM modules WHERE status = :status")
    suspend fun countByStatus(status: String): Int
}
