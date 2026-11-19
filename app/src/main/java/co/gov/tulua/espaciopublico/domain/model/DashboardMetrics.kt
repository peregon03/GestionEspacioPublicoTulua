package co.gov.tulua.espaciopublico.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DashboardMetrics(
    val totalModules: Int,
    val weeklyDelta: Int,
    val formalCount: Int,
    val informalCount: Int,
    val occupiedToday: Int,
    val occupancyPercent: Float,
    val pendingRequests: Int,
    val sectors: List<Sector>,
    val upcomingEvents: List<Event>,
)
