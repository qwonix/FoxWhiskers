package ru.qwonix.android.foxwhiskers.service

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import ru.qwonix.android.foxwhiskers.dataStore
import ru.qwonix.android.foxwhiskers.entity.UserProfile


class LocalUserStorageService(private val context: Context) {

    private companion object {
        val FIRST_NAME = stringPreferencesKey("first_name")
        val LAST_NAME = stringPreferencesKey("last_name")
        val EMAIL = stringPreferencesKey("email")
        val PHONE_NUMBER = stringPreferencesKey("phone_number")
    }


    suspend fun loadUserProfile(): UserProfile? {
        val preferences = context.dataStore.data.firstOrNull()
        return if (preferences == null || !preferences.contains(FIRST_NAME)) {
            null
        } else {
            UserProfile(
                firstName = preferences[FIRST_NAME],
                lastName = preferences[LAST_NAME],
                email = preferences[EMAIL],
                phoneNumber = preferences[PHONE_NUMBER] ?: ""
            )
        }
    }

    suspend fun saveUserProfile(userProfile: UserProfile) {
        context.dataStore.edit { preferences ->
            preferences[FIRST_NAME] = userProfile.firstName ?: ""
            preferences[LAST_NAME] = userProfile.lastName ?: ""
            preferences[EMAIL] = userProfile.email ?: ""
            preferences[PHONE_NUMBER] = userProfile.phoneNumber
        }
    }

    suspend fun clearUserProfile() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}