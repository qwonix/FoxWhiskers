package ru.qwonix.android.foxwhiskers.repository

import ru.qwonix.android.foxwhiskers.entity.PaymentMethod
import ru.qwonix.android.foxwhiskers.entity.PickUpLocationSettings
import ru.qwonix.android.foxwhiskers.service.LocalSettingsService
import ru.qwonix.android.foxwhiskers.service.MenuService
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val menuService: MenuService,
    private val localSettingsService: LocalSettingsService
) {
    fun load() = requestFlow {
        val loadSettings = localSettingsService.loadSettings()
        if (loadSettings.paymentMethod == null) {
            loadSettings.paymentMethod = PaymentMethod.INAPP_ONLINE_CARD
        }
        if (loadSettings.pickUpLocationSettings == null) {
            val response = menuService.findAllLocations()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                val maxPriorityPickUpLocation = body.maxBy { it.priority }

                loadSettings.pickUpLocationSettings = PickUpLocationSettings(maxPriorityPickUpLocation)
            }
        }
        loadSettings
    }
}