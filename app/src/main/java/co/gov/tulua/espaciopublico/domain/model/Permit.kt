package co.gov.tulua.espaciopublico.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Permit(
    val id: Long,
    val type: String,
    val number: String,
    val issuedAt: String,
    val expiresAt: String,
    val isActive: Boolean,
)
