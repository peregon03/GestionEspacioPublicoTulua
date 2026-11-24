package co.gov.tulua.espaciopublico.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Event(
    val id: Long,
    val code: String,
    val name: String,
    val status: EventStatus,
    val startDate: String,
    val endDate: String,
    val location: GeoLocation,
    val organizer: String,
    val comuna: String? = null,
    val totalSpots: Int,
    val assignedSpots: Int,
    val zoneId: Long? = null,
)
