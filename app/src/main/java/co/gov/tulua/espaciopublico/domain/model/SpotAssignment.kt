package co.gov.tulua.espaciopublico.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SpotAssignment(
    val id: Long,
    val eventId: Long,
    val spotLabel: String,
    val vendorId: Long? = null,
    val vendorName: String? = null,
    val moduleId: Long? = null,
)
