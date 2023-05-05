package ru.qwonix.android.foxwhiskers.entity

data class OrderItem(
    val dish: Dish,
    val quantity: Int,
    val pricePerItem: Double
)
