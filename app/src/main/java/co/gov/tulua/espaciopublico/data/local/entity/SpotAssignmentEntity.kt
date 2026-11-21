package co.gov.tulua.espaciopublico.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "spot_assignments",
    foreignKeys = [
        ForeignKey(
            entity = EventEntity::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("eventId")],
)
data class SpotAssignmentEntity(
    @PrimaryKey val id: Long,
    val eventId: Long,
    val spotLabel: String,
    val vendorId: Long?,
    val vendorName: String?,
    val moduleId: Long?,
)
