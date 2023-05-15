package ru.qwonix.android.foxwhiskers.dto

data class OrderCreationRequestDTO(
    val phoneNumber: String,
    val orderItems: List<OrderCreationItemDTO>,
    val pickUpLocationId: Long
)

data class OrderCreationItemDTO(
    private val dishId: Long,
    private val count: Int
)