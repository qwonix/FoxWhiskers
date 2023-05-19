package ru.qwonix.android.foxwhiskers.entity

data class Settings(
    val pickUpLocationSetting: PickUpLocationSetting?,
    val paymentMethod: PaymentMethod?
) {
    constructor() : this(null, null)
}

data class PickUpLocationSetting(
    val pickUpLocationId: Long,
    val title: String,
    val description: String
)
