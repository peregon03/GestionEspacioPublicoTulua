package co.gov.tulua.espaciopublico.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import co.gov.tulua.espaciopublico.data.local.entity.RequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RequestDao {

    @Query("SELECT * FROM requests ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<RequestEntity>>

    @Query(
        """
        SELECT * FROM requests
        WHERE (:status IS NULL OR status = :status)
        AND (:search IS NULL OR code LIKE '%' || :search || '%' OR applicant LIKE '%' || :search || '%' OR applicantId LIKE '%' || :search || '%')
        ORDER BY createdAt DESC
        LIMIT :size OFFSET (:page - 1) * :size
        """
    )
    suspend fun getRequests(
        status: String?,
        search: String?,
        page: Int,
        size: Int,
    ): List<RequestEntity>

    @Query("SELECT * FROM requests WHERE id = :id")
    suspend fun getById(id: Long): RequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(requests: List<RequestEntity>)

    @Update
    suspend fun update(request: RequestEntity)

    @Query("DELETE FROM requests")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM requests WHERE status = :status")
    suspend fun countByStatus(status: String): Int
}
