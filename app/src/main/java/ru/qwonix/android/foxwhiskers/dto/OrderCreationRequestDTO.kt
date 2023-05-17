package ru.qwonix.android.foxwhiskers.dto

import ru.qwonix.android.foxwhiskers.entity.PaymentMethod

data class OrderCreationRequestDTO(
    val phoneNumber: String,
    val orderItems: List<OrderCreationItemDTO>,
    val pickUpLocationId: Long,
    val paymentMethod: PaymentMethod
)

data class OrderCreationItemDTO(
    private val dishId: Long,
    private val count: Int
)