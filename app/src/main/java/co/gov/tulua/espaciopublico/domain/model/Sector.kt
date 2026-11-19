package co.gov.tulua.espaciopublico.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Sector(
    val id: Long,
    val name: String,
    val description: String? = null,
    val moduleCount: Int,
    val occupancyPercent: Float,
    val saturationLevel: SaturationLevel,
)
