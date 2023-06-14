package ru.qwonix.android.foxwhiskers.service

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import ru.qwonix.android.foxwhiskers.clientDataStore
import ru.qwonix.android.foxwhiskers.entity.Client


class LocalClientService(private val context: Context) {

    private companion object {
        val FIRST_NAME = stringPreferencesKey("first_name")
        val LAST_NAME = stringPreferencesKey("last_name")
        val EMAIL = stringPreferencesKey("email")
        val PHONE_NUMBER = stringPreferencesKey("phone_number")
    }


    suspend fun loadClientProfile(): Client? {
        val preferences = context.clientDataStore.data.firstOrNull()
        return if (preferences == null || !preferences.contains(FIRST_NAME)) {
            null
        } else {
            Client(
                firstName = preferences[FIRST_NAME],
                lastName = preferences[LAST_NAME],
                email = preferences[EMAIL],
                phoneNumber = preferences[PHONE_NUMBER] ?: ""
            )
        }
    }

    suspend fun saveUserProfile(client: Client) {
        context.clientDataStore.edit { preferences ->
            preferences[FIRST_NAME] = client.firstName ?: ""
            preferences[LAST_NAME] = client.lastName ?: ""
            preferences[EMAIL] = client.email ?: ""
            preferences[PHONE_NUMBER] = client.phoneNumber
        }
    }

    suspend fun clearUserProfile() {
        context.clientDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}