package co.gov.tulua.espaciopublico.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ReportData(
    val period: String,
    val sectorId: Long? = null,
    val modulesBySector: Map<String, Int> = emptyMap(),
    val formalVsInformalSeries: List<FormalInformalPoint> = emptyList(),
    val eventOccupancy: List<EventOccupancyPoint> = emptyList(),
    val requestsByStatus: Map<String, Int> = emptyMap(),
)

@Serializable
data class FormalInformalPoint(
    val month: String,
    val formal: Int,
    val informal: Int,
)

@Serializable
data class EventOccupancyPoint(
    val eventName: String,
    val totalSpots: Int,
    val assignedSpots: Int,
)
