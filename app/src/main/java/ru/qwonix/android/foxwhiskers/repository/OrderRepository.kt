package ru.qwonix.android.foxwhiskers.repository

import ru.qwonix.android.foxwhiskers.dto.OrderCreationRequestDTO
import ru.qwonix.android.foxwhiskers.service.OrderService

class OrderRepository(
    private val orderService: OrderService
) {

    fun create(orderCreationRequestDTO: OrderCreationRequestDTO) = apiRequestFlow {
        orderService.create(orderCreationRequestDTO)
    }

    fun all(phoneNumber: String) = apiRequestFlow {
        orderService.all(phoneNumber)
    }

}