package co.gov.tulua.espaciopublico.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Sanction(
    val id: Long,
    val type: SanctionType,
    val description: String,
    val issuedAt: String,
    val resolvedAt: String? = null,
    val isActive: Boolean,
)
