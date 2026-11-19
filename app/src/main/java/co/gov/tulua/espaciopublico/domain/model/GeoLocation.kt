package co.gov.tulua.espaciopublico.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class GeoLocation(
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val sectorId: Long? = null,
    val zone: String? = null,
)
