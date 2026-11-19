package co.gov.tulua.espaciopublico.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val name: String,
    val email: String,
    val role: UserRole,
    val token: String? = null,
    val refreshToken: String? = null,
)
