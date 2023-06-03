package ru.qwonix.android.foxwhiskers.entity

import java.time.LocalDateTime

data class Order(
    val id: String,
    val client: Client,
    val orderItems: List<OrderItem>,
    val status: OrderStatus,
    val pickUpLocation: PickUpLocation,
    val paymentMethod: PaymentMethod,
    val totalPrice: Double,
    val expectedReceiptTime: String,
    val created: LocalDateTime
)



