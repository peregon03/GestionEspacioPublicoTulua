package co.gov.tulua.espaciopublico.data.mock

import co.gov.tulua.espaciopublico.domain.model.SaturationLevel
import co.gov.tulua.espaciopublico.domain.model.Sector
import co.gov.tulua.espaciopublico.domain.repository.SectorRepository
import javax.inject.Inject

class MockSectorRepository @Inject constructor() : SectorRepository {

    private val sectors = listOf(
        Sector(
            id = 1L,
            name = "Carrera 25",
            description = "Corredor comercial principal del centro de Tuluá",
            moduleCount = 48,
            occupancyPercent = 85f,
            saturationLevel = SaturationLevel.ALTA,
        ),
        Sector(
            id = 2L,
            name = "Galería y Plaza de Mercado",
            description = "Zona de comercio tradicional y abastecimiento",
            moduleCount = 62,
            occupancyPercent = 92f,
            saturationLevel = SaturationLevel.CRITICA,
        ),
        Sector(
            id = 3L,
            name = "Parque Boyacá",
            description = "Parque central con comercio informal circundante",
            moduleCount = 25,
            occupancyPercent = 60f,
            saturationLevel = SaturationLevel.MEDIA,
        ),
        Sector(
            id = 4L,
            name = "Parque Céspedes",
            description = "Zona de esparcimiento con ventas ambulantes los fines de semana",
            moduleCount = 18,
            occupancyPercent = 40f,
            saturationLevel = SaturationLevel.BAJA,
        ),
        Sector(
            id = 5L,
            name = "Plaza de Mercado Central",
            description = "Centro de acopio y distribución de alimentos",
            moduleCount = 55,
            occupancyPercent = 78f,
            saturationLevel = SaturationLevel.ALTA,
        ),
    )

    override suspend fun getSectors(): Result<List<Sector>> = Result.success(sectors)

    override suspend fun getSectorById(id: Long): Result<Sector> {
        val sector = sectors.find { it.id == id }
            ?: return Result.failure(NoSuchElementException("Sector $id no encontrado"))
        return Result.success(sector)
    }
}
