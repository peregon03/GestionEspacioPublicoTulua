package co.gov.tulua.espaciopublico.data.remote

import co.gov.tulua.espaciopublico.data.local.session.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * Interceptor OkHttp que añade el header `Authorization: Bearer <token>`
 * en cada petición que no sea de autenticación (login/register).
 */
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        // Rutas de autenticación no necesitan el token
        if (AUTH_PATHS.any { path.contains(it) }) {
            return chain.proceed(originalRequest)
        }

        val token = runBlocking { sessionManager.token.first() }
            ?: return chain.proceed(originalRequest)

        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }

    companion object {
        private val AUTH_PATHS = listOf("/auth/login", "/auth/register")
    }
}
