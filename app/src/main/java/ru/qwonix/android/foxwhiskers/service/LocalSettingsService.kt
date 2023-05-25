package ru.qwonix.android.foxwhiskers.service

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.firstOrNull
import ru.qwonix.android.foxwhiskers.entity.PaymentMethod
import ru.qwonix.android.foxwhiskers.entity.PickUpLocation
import ru.qwonix.android.foxwhiskers.settingsDataStore


class LocalSettingsService(private val gson: Gson, private val context: Context) {

    private companion object {
        val PAYMENT_METHOD = stringPreferencesKey("payment_method")
        val PICK_UP_LOCATION = stringPreferencesKey("pick_up_location_id")
    }

    suspend fun loadSelectedPickUpLocation(): PickUpLocation? {
        val preferences = context.settingsDataStore.data.firstOrNull()
        return if (preferences == null || !preferences.contains(PICK_UP_LOCATION)) {
            null
        } else {
            val type = object : TypeToken<PickUpLocation>() {}.type
            return gson.fromJson(preferences[PICK_UP_LOCATION], type)
        }
    }

    suspend fun loadSelectedPaymentMethod(): PaymentMethod? {
        val preferences = context.settingsDataStore.data.firstOrNull()
        return if (preferences == null) {
            null
        } else {
            preferences[PAYMENT_METHOD]?.let { PaymentMethod.valueOf(it) }
        }
    }


    suspend fun save(pickUpLocation: PickUpLocation) {
        context.settingsDataStore.edit { preferences ->
            preferences[PICK_UP_LOCATION] = gson.toJson(pickUpLocation)
        }
    }

    suspend fun saveSelectedPaymentMethod(paymentMethod: PaymentMethod) {
        context.settingsDataStore.edit { preferences ->
            preferences[PAYMENT_METHOD] = paymentMethod.toString()
        }
    }

    suspend fun clearSettings() {
        context.settingsDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}