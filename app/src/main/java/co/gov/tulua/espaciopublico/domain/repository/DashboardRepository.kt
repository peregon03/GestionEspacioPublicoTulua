package co.gov.tulua.espaciopublico.domain.repository

import co.gov.tulua.espaciopublico.domain.model.DashboardMetrics
import co.gov.tulua.espaciopublico.domain.model.Sector

interface DashboardRepository {
    suspend fun getMetrics(): Result<DashboardMetrics>
    suspend fun getSectors(): Result<List<Sector>>
}
