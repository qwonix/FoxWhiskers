package ru.qwonix.android.foxwhiskers.entity

import java.time.LocalDateTime
import java.util.UUID

data class Order(
    val id: UUID,
    val client: Client,
    val status: OrderStatus,
    val pickUpLocation: PickUpLocation,
    val orderItems: List<OrderItem>,
    val paymentMethod: PaymentMethod,
    val totalPrice: Double,
    val expectedReceiptTime: LocalDateTime?,
    val created: LocalDateTime
)



