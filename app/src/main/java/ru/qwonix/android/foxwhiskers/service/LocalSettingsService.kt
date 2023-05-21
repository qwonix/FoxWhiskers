package ru.qwonix.android.foxwhiskers.service

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import ru.qwonix.android.foxwhiskers.entity.PaymentMethod
import ru.qwonix.android.foxwhiskers.entity.PickUpLocation
import ru.qwonix.android.foxwhiskers.settingsDataStore


class LocalSettingsService(private val context: Context) {

    private companion object {
        val PAYMENT_METHOD = stringPreferencesKey("payment_method")
        val PICK_UP_LOCATION_ID = longPreferencesKey("pick_up_location_id")
        val PICK_UP_LOCATION_TITLE = stringPreferencesKey("pick_up_location_title")
        val PICK_UP_LOCATION_PRIORITY = intPreferencesKey("pick_up_location_priority")
        val PICK_UP_LOCATION_CITY_NAME = stringPreferencesKey("pick_up_location_city_name")
        val PICK_UP_LOCATION_STREET_NAME = stringPreferencesKey("pick_up_location_street_name")
        val PICK_UP_LOCATION_HOUSE_DATA = stringPreferencesKey("pick_up_location_house_data")
        val PICK_UP_LOCATION_ADDITIONAL_INFORMATION =
            stringPreferencesKey("pick_up_location_additional_information")
        val PICK_UP_LOCATION_LATITUDE = doublePreferencesKey("pick_up_location_latitude")
        val PICK_UP_LOCATION_LONGITUDE = doublePreferencesKey("pick_up_location_longitude")
    }


    suspend fun loadSelectedPickUpLocation(): PickUpLocation? {
        val preferences = context.settingsDataStore.data.firstOrNull()
        if (preferences == null) {
            return null
        } else {
            val id = preferences[PICK_UP_LOCATION_ID]
            val title = preferences[PICK_UP_LOCATION_TITLE]
            val priority = preferences[PICK_UP_LOCATION_PRIORITY]
            val cityName = preferences[PICK_UP_LOCATION_CITY_NAME]
            val streetName = preferences[PICK_UP_LOCATION_STREET_NAME]
            val houseData = preferences[PICK_UP_LOCATION_HOUSE_DATA]
            val additionalInformation = preferences[PICK_UP_LOCATION_ADDITIONAL_INFORMATION]
            val latitude = preferences[PICK_UP_LOCATION_LATITUDE]
            val longitude = preferences[PICK_UP_LOCATION_LONGITUDE]

            if (id != null &&
                title != null &&
                priority != null &&
                cityName != null &&
                streetName != null &&
                houseData != null &&
                additionalInformation != null &&
                latitude != null &&
                longitude != null
            ) {
                val pickUpLocation = PickUpLocation(
                    id,
                    title,
                    priority,
                    cityName,
                    streetName,
                    houseData,
                    additionalInformation,
                    latitude,
                    longitude
                )
                return pickUpLocation
            }

            return null
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


    suspend fun saveSelectedPickUpLocation(pickUpLocation: PickUpLocation) {
        context.settingsDataStore.edit { preferences ->
            preferences[PICK_UP_LOCATION_ID] = pickUpLocation.id
            preferences[PICK_UP_LOCATION_TITLE] = pickUpLocation.title
            preferences[PICK_UP_LOCATION_PRIORITY] = pickUpLocation.priority
            preferences[PICK_UP_LOCATION_CITY_NAME] = pickUpLocation.cityName
            preferences[PICK_UP_LOCATION_STREET_NAME] = pickUpLocation.streetName
            preferences[PICK_UP_LOCATION_HOUSE_DATA] = pickUpLocation.houseData
            preferences[PICK_UP_LOCATION_ADDITIONAL_INFORMATION] =
                pickUpLocation.additionalInformation
            preferences[PICK_UP_LOCATION_LATITUDE] = pickUpLocation.latitude
            preferences[PICK_UP_LOCATION_LONGITUDE] = pickUpLocation.longitude
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