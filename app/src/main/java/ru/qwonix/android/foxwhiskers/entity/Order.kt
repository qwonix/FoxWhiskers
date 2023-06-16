package ru.qwonix.android.foxwhiskers.entity

data class Order(
    val id: String,
    val client: Client,
    val orderItems: List<OrderItem>,
    val status: OrderStatus,
    val pickUpLocation: PickUpLocation,
    val qrCodeData: String,
    val paymentMethod: PaymentMethod,
    val totalPrice: Double,
    val expectedReceiptTime: String
)



