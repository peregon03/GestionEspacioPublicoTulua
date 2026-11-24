package co.gov.tulua.espaciopublico.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Vendor(
    val id: Long,
    val fullName: String,
    val idNumber: String,
    val phone: String? = null,
    val email: String? = null,
    val association: String? = null,
)
