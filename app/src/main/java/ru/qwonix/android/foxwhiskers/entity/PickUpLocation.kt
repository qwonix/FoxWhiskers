package ru.qwonix.android.foxwhiskers.entity

data class PickUpLocation(
    val id: Long,
    val title: String,
    val priority: Int,
    val cityName: String,
    val streetName: String,
    val houseData: String,
    val additionalInformation: String,
    val latitude: Double,
    val longitude: Double,
)
