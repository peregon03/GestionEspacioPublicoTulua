package co.gov.tulua.espaciopublico.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthCredentials(
    val identifier: String,
    val password: String,
    val role: UserRole,
)

@Serializable
data class AuthResponse(
    val user: User,
    val token: String,
    val refreshToken: String,
)
