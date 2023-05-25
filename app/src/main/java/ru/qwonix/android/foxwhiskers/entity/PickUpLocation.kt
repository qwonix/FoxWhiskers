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
    val longitude: Double
) {


    val description: String
        get() = "$streetName $houseData, $cityName"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PickUpLocation

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}