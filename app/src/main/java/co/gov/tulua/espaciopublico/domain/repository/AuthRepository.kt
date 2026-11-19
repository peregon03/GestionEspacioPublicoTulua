package co.gov.tulua.espaciopublico.domain.repository

import co.gov.tulua.espaciopublico.domain.model.AuthCredentials
import co.gov.tulua.espaciopublico.domain.model.AuthResponse
import co.gov.tulua.espaciopublico.domain.model.User

interface AuthRepository {
    suspend fun login(credentials: AuthCredentials): Result<AuthResponse>
    suspend fun refreshToken(refreshToken: String): Result<AuthResponse>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun resetPassword(token: String, newPassword: String): Result<Unit>
    suspend fun getCurrentUser(): Result<User>
    suspend fun logout()
}
