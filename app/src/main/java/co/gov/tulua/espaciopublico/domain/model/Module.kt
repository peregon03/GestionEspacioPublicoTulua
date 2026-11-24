package co.gov.tulua.espaciopublico.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Module(
    val id: Long,
    val code: String,
    val name: String,
    val type: ModuleType,
    val category: String,
    val status: ModuleStatus,
    val vendor: Vendor,
    val location: GeoLocation,
    val areaM2: Double,
    val widthM: Double? = null,
    val lengthM: Double? = null,
    val photoUrl: String? = null,
    val validUntil: String? = null,
    val lastVerifiedAt: String? = null,
    val observations: String? = null,
    val permits: List<Permit> = emptyList(),
    val documents: List<Document> = emptyList(),
    val sanctions: List<Sanction> = emptyList(),
)
