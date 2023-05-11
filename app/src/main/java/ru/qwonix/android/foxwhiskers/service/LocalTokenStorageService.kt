package ru.qwonix.android.foxwhiskers.service

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.qwonix.android.foxwhiskers.authenticationDataStore

class LocalTokenStorageService(private val context: Context) {

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("jwt_access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("jwt_refresh_token")
    }

    fun getAccessToken(): Flow<String?> {
        return context.authenticationDataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }
    }

    fun getRefreshToken(): Flow<String?> {
        return context.authenticationDataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }
    }

    suspend fun saveAccessToken(accessToken: String) {
        context.authenticationDataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
        }
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        context.authenticationDataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    suspend fun clearAccessToken() {
        context.authenticationDataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
        }
    }

    suspend fun clearRefreshToken() {
        context.authenticationDataStore.edit { preferences ->
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }
}
