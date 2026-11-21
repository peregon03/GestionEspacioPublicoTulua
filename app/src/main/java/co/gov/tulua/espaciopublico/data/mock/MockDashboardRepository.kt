package co.gov.tulua.espaciopublico.data.mock

import co.gov.tulua.espaciopublico.domain.model.DashboardMetrics
import co.gov.tulua.espaciopublico.domain.model.Sector
import co.gov.tulua.espaciopublico.domain.repository.DashboardRepository
import javax.inject.Inject

class MockDashboardRepository @Inject constructor(
    private val sectorRepo: MockSectorRepository,
    private val eventRepo: MockEventRepository,
) : DashboardRepository {

    override suspend fun getMetrics(): Result<DashboardMetrics> {
        val sectors = sectorRepo.getSectors().getOrDefault(emptyList())
        val events = eventRepo.getEvents(null, null).getOrDefault(emptyList())
        val upcomingEvents = events
            .filter { it.status.name != "FINALIZADA" }
            .sortedBy { it.startDate }
            .take(3)

        val totalModules = sectors.sumOf { it.moduleCount }
        val formalCount = 24  // matches ~60% of 40 modules
        val informalCount = 16

        return Result.success(
            DashboardMetrics(
                totalModules = totalModules,
                weeklyDelta = 3,
                formalCount = formalCount,
                informalCount = informalCount,
                occupiedToday = 35,
                occupancyPercent = 72.5f,
                pendingRequests = 6,
                sectors = sectors,
                upcomingEvents = upcomingEvents,
            )
        )
    }

    override suspend fun getSectors(): Result<List<Sector>> = sectorRepo.getSectors()
}
