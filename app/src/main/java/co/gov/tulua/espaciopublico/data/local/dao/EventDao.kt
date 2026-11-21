package co.gov.tulua.espaciopublico.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import co.gov.tulua.espaciopublico.data.local.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events ORDER BY startDate ASC")
    fun observeAll(): Flow<List<EventEntity>>

    @Query(
        """
        SELECT * FROM events
        WHERE (:month IS NULL OR startDate LIKE :month || '%' OR endDate LIKE :month || '%')
        AND (:status IS NULL OR status = :status)
        ORDER BY startDate ASC
        """
    )
    suspend fun getEvents(month: String?, status: String?): List<EventEntity>

    @Query("SELECT * FROM events WHERE id = :id")
    suspend fun getById(id: Long): EventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Update
    suspend fun update(event: EventEntity)

    @Query("DELETE FROM events")
    suspend fun deleteAll()
}
