package co.gov.tulua.espaciopublico.data.local.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import co.gov.tulua.espaciopublico.domain.model.User
import co.gov.tulua.espaciopublico.domain.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[KEY_TOKEN] != null
    }

    val currentUser: Flow<User?> = dataStore.data.map { prefs ->
        val id = prefs[KEY_USER_ID] ?: return@map null
        val name = prefs[KEY_USER_NAME] ?: return@map null
        val email = prefs[KEY_USER_EMAIL] ?: return@map null
        val role = prefs[KEY_USER_ROLE] ?: return@map null
        User(
            id = id,
            name = name,
            email = email,
            role = UserRole.valueOf(role),
            token = prefs[KEY_TOKEN],
            refreshToken = prefs[KEY_REFRESH_TOKEN],
        )
    }

    val token: Flow<String?> = dataStore.data.map { prefs ->
        prefs[KEY_TOKEN]
    }

    suspend fun saveSession(user: User, token: String, refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = user.id
            prefs[KEY_USER_NAME] = user.name
            prefs[KEY_USER_EMAIL] = user.email
            prefs[KEY_USER_ROLE] = user.role.name
            prefs[KEY_TOKEN] = token
            prefs[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun updateTokens(token: String, refreshToken: String) {
        dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun getToken(): String? = dataStore.data.first()[KEY_TOKEN]

    suspend fun getRefreshToken(): String? = dataStore.data.first()[KEY_REFRESH_TOKEN]

    suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }

    companion object {
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_ROLE = stringPreferencesKey("user_role")
        private val KEY_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }
}
