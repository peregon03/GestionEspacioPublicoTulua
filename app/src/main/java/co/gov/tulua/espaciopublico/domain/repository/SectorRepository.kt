package co.gov.tulua.espaciopublico.domain.repository

import co.gov.tulua.espaciopublico.domain.model.Sector

interface SectorRepository {
    suspend fun getSectors(): Result<List<Sector>>
    suspend fun getSectorById(id: Long): Result<Sector>
}
