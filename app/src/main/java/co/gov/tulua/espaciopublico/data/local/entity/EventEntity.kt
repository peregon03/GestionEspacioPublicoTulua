package co.gov.tulua.espaciopublico.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: Long,
    val code: String,
    val name: String,
    val status: String,
    val startDate: String,
    val endDate: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val locationSectorId: Long?,
    val locationZone: String?,
    val organizer: String,
    val comuna: String?,
    val totalSpots: Int,
    val assignedSpots: Int,
    val zoneId: Long?,
)
