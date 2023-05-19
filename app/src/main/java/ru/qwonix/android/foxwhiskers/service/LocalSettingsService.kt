package ru.qwonix.android.foxwhiskers.service

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import ru.qwonix.android.foxwhiskers.entity.PaymentMethod
import ru.qwonix.android.foxwhiskers.entity.PickUpLocationSetting
import ru.qwonix.android.foxwhiskers.entity.Settings
import ru.qwonix.android.foxwhiskers.settingsDataStore


class LocalSettingsService(private val context: Context) {

    private companion object {
        val PAYMENT_METHOD = stringPreferencesKey("payment_method")
        val PICK_UP_LOCATION_ID = longPreferencesKey("pick_up_location_id")
        val PICK_UP_LOCATION_TITLE = stringPreferencesKey("pick_up_location_title")
        val PICK_UP_LOCATION_DESCRIPTION = stringPreferencesKey("pick_up_location_description")
    }


    suspend fun loadSettings(): Settings {
        val preferences = context.settingsDataStore.data.firstOrNull()
        if (preferences == null) {
            return Settings()
        } else {
            val pickUpLocationId = preferences[PICK_UP_LOCATION_ID]
            val title = preferences[PICK_UP_LOCATION_TITLE]
            val description = preferences[PICK_UP_LOCATION_DESCRIPTION]

            val pickUpLocationSetting =
                if (pickUpLocationId != null && title != null && description != null) {
                    PickUpLocationSetting(pickUpLocationId, title, description)
                } else {
                    null
                }

            val paymentMethod = preferences[PAYMENT_METHOD]?.let { PaymentMethod.valueOf(it) }

            return Settings(
                pickUpLocationSetting = pickUpLocationSetting,
                paymentMethod = paymentMethod
            )
        }
    }

    suspend fun saveSettings(settings: Settings) {
        context.settingsDataStore.edit { preferences ->
            if (settings.pickUpLocationSetting != null) {
                preferences[PICK_UP_LOCATION_ID] = settings.pickUpLocationSetting.pickUpLocationId
                preferences[PICK_UP_LOCATION_TITLE] = settings.pickUpLocationSetting.title
                preferences[PICK_UP_LOCATION_DESCRIPTION] =
                    settings.pickUpLocationSetting.description
            }
            if (settings.paymentMethod != null) {
                preferences[PAYMENT_METHOD] = settings.paymentMethod.toString()
            }
        }
    }

    suspend fun clearSettings() {
        context.settingsDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}