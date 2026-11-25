package co.gov.tulua.espaciopublico.data.remote

import co.gov.tulua.espaciopublico.data.local.session.SessionManager
import co.gov.tulua.espaciopublico.domain.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

/**
 * Authenticator OkHttp para respuestas 401.
 *
 * Intenta refrescar el token usando [AuthRepository.refreshToken].
 * Si el refresh falla, limpia la sesión para forzar redirección al login.
 */
class AuthAuthenticator @Inject constructor(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Evitar loop infinito si ya se reintentó con un token refrescado
        if (response.responseCount() > 1) return null

        val refreshToken = runBlocking { sessionManager.getRefreshToken() }
            ?: run {
                runBlocking { sessionManager.clearSession() }
                return null
            }

        return runBlocking {
            authRepository.refreshToken(refreshToken)
                .onSuccess { authResponse ->
                    sessionManager.updateTokens(
                        token = authResponse.token,
                        refreshToken = authResponse.refreshToken,
                    )
                }
                .onFailure {
                    sessionManager.clearSession()
                }
                .getOrNull()
                ?.let { authResponse ->
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${authResponse.token}")
                        .build()
                }
        }
    }

    // Cuenta cuántas respuestas anteriores hubo (para detectar loops)
    private fun Response.responseCount(): Int {
        var count = 0
        var resp: Response? = this
        while (resp != null) {
            count++
            resp = resp.priorResponse
        }
        return count
    }
}
