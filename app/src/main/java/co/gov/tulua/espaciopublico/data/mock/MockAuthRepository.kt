package co.gov.tulua.espaciopublico.data.mock

import co.gov.tulua.espaciopublico.domain.model.AuthCredentials
import co.gov.tulua.espaciopublico.domain.model.AuthResponse
import co.gov.tulua.espaciopublico.domain.model.User
import co.gov.tulua.espaciopublico.domain.model.UserRole
import co.gov.tulua.espaciopublico.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockAuthRepository @Inject constructor() : AuthRepository {

    // Estado interno de sesión mock
    private var currentUser: User? = null
    private var currentToken: String? = null

    override suspend fun login(credentials: AuthCredentials): Result<AuthResponse> {
        delay(800)

        // En mock cualquier credencial con contraseña no vacía es válida
        val user = User(
            id = 1L,
            name = mockNameForRole(credentials.role),
            email = credentials.identifier,
            role = credentials.role,
        )
        val token = "mock_jwt_${System.currentTimeMillis()}"
        val refreshToken = "mock_refresh_${System.currentTimeMillis()}"

        currentUser = user
        currentToken = token

        return Result.success(
            AuthResponse(
                user = user,
                token = token,
                refreshToken = refreshToken,
            )
        )
    }

    override suspend fun refreshToken(refreshToken: String): Result<AuthResponse> {
        delay(300)
        val user = currentUser
            ?: return Result.failure(IllegalStateException("No hay sesión activa"))

        val newToken = "mock_jwt_refreshed_${System.currentTimeMillis()}"
        val newRefresh = "mock_refresh_refreshed_${System.currentTimeMillis()}"
        currentToken = newToken

        return Result.success(
            AuthResponse(
                user = user,
                token = newToken,
                refreshToken = newRefresh,
            )
        )
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        delay(600)
        return Result.success(Unit)
    }

    override suspend fun resetPassword(token: String, newPassword: String): Result<Unit> {
        delay(600)
        return Result.success(Unit)
    }

    override suspend fun getCurrentUser(): Result<User> {
        val user = currentUser
            ?: return Result.failure(IllegalStateException("No hay sesión activa"))
        return Result.success(user)
    }

    override suspend fun logout() {
        currentUser = null
        currentToken = null
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private fun mockNameForRole(role: UserRole): String = when (role) {
        UserRole.ADMINISTRADOR -> "Admin Mock"
        UserRole.INSPECTOR     -> "Inspector Mock"
        UserRole.COMERCIANTE   -> "Comerciante Mock"
    }
}
