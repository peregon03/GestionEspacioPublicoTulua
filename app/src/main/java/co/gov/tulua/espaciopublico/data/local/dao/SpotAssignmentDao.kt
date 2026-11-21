package co.gov.tulua.espaciopublico.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.gov.tulua.espaciopublico.data.local.entity.SpotAssignmentEntity

@Dao
interface SpotAssignmentDao {

    @Query("SELECT * FROM spot_assignments WHERE eventId = :eventId ORDER BY spotLabel ASC")
    suspend fun getByEventId(eventId: Long): List<SpotAssignmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(assignment: SpotAssignmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(assignments: List<SpotAssignmentEntity>)

    @Query("DELETE FROM spot_assignments WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM spot_assignments WHERE eventId = :eventId")
    suspend fun deleteByEventId(eventId: Long)
}
