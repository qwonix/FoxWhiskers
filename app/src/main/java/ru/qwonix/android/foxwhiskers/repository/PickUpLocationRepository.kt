package ru.qwonix.android.foxwhiskers.repository

import ru.qwonix.android.foxwhiskers.entity.PickUpLocation
import ru.qwonix.android.foxwhiskers.service.LocalSettingsService
import ru.qwonix.android.foxwhiskers.service.PickUpLocationService
import javax.inject.Inject

class PickUpLocationRepository @Inject constructor(
    private val pickUpLocationService: PickUpLocationService,
    private val settingsService: LocalSettingsService
) {

    fun selected() = requestFlow {
        var selectedPickUpLocation = settingsService.loadSelectedPickUpLocation()
        if (selectedPickUpLocation == null) {
            val response = pickUpLocationService.findMaxPriority()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                selectedPickUpLocation = body
            }
        }
        selectedPickUpLocation
    }

    fun setSelected(pickUpLocation: PickUpLocation) = requestFlow {
        settingsService.saveSelectedPickUpLocation(pickUpLocation)
    }

    fun findAllLocations() = apiRequestFlow {
        pickUpLocationService.findAll()
    }

}