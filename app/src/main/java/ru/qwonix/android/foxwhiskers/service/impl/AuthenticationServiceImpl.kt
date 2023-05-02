package ru.qwonix.android.foxwhiskers.service.impl

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import ru.qwonix.android.foxwhiskers.AuthenticationResponseDTO
import ru.qwonix.android.foxwhiskers.entity.UserProfile
import ru.qwonix.android.foxwhiskers.repository.AuthenticationRepository
import ru.qwonix.android.foxwhiskers.repository.ResponseDao
import ru.qwonix.android.foxwhiskers.service.AuthenticationService

class AuthenticationServiceImpl(
    private val context: Context,
    private val authenticationRepository: AuthenticationRepository
) :
    AuthenticationService {

    private val Context.dataStore by preferencesDataStore(name = "authentication")

    private companion object {
        val FIRST_NAME = stringPreferencesKey("first_name")
        val LAST_NAME = stringPreferencesKey("last_name")
        val EMAIL = stringPreferencesKey("email")
        val PHONE_NUMBER = stringPreferencesKey("phone_number")
        val JWT_ACCESS_TOKEN = stringPreferencesKey("jwt_access_token")
        val JWT_REFRESH_TOKEN = stringPreferencesKey("jwt_refresh_token")
    }


    override suspend fun loadUserProfile(): UserProfile? {
        val preferences = context.dataStore.data.firstOrNull()
        if (preferences == null || !preferences.contains(FIRST_NAME)) {
            return null
        } else {
            return UserProfile(
                firstName = preferences[FIRST_NAME],
                lastName = preferences[LAST_NAME],
                email = preferences[EMAIL],
                phoneNumber = preferences[PHONE_NUMBER] ?: "",
                jwtAccessToken = preferences[JWT_ACCESS_TOKEN] ?: "",
                jwtRefreshToken = preferences[JWT_REFRESH_TOKEN] ?: ""
            )
        }
    }

    override suspend fun saveUserProfile(userProfile: UserProfile) {
        context.dataStore.edit { preferences ->
            preferences[FIRST_NAME] = userProfile.firstName ?: ""
            preferences[LAST_NAME] = userProfile.lastName ?: ""
            preferences[EMAIL] = userProfile.email ?: ""
            preferences[PHONE_NUMBER] = userProfile.phoneNumber
            preferences[JWT_ACCESS_TOKEN] = userProfile.jwtAccessToken
            preferences[JWT_REFRESH_TOKEN] = userProfile.jwtRefreshToken
        }
    }

    override suspend fun clearUserProfile() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    override suspend fun updateProfile(userProfile: UserProfile): ResponseDao<UserProfile?> {
        return authenticationRepository.updateProfile(userProfile)
    }

    override suspend fun loadUserProfile(
        phoneNumber: String,
        jwtAccessToken: String
    ): ResponseDao<UserProfile?> {
        return authenticationRepository.loadUserProfile(phoneNumber, jwtAccessToken)
    }

    override suspend fun authenticate(
        phoneNumber: String,
        code: Int
    ): ResponseDao<AuthenticationResponseDTO?> {
        return authenticationRepository.authenticate(phoneNumber, code)
    }

    override suspend fun sendAuthenticationSmsCodeToNumber(phoneNumber: String) {
        authenticationRepository.sendAuthenticationSmsCodeToNumber(phoneNumber)
    }


}
