package ru.qwonix.android.foxwhiskers.entity

data class Location(
    val title: String,
    val cityName: String,
    val streetName: String,
    val houseData: String,
    val additionalInformation: String,
    val latitude: Double,
    val longitude: Double,
)
