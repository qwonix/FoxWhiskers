package ru.qwonix.android.foxwhiskers.entity

data class Settings(
    var pickUpLocationSettings: PickUpLocationSettings?,
    var paymentMethod: PaymentMethod?
) {
    constructor() : this(null, null)
}

data class PickUpLocationSettings(
    val pickUpLocationId: Long,
    val title: String,
    val description: String
) {
    constructor(pickUpLocation: PickUpLocation) : this(
        pickUpLocation.id,
        pickUpLocation.title,
        pickUpLocation.description
    )
}
