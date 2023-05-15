package ru.qwonix.android.foxwhiskers.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import ru.qwonix.android.foxwhiskers.dto.OrderCreationRequestDTO
import ru.qwonix.android.foxwhiskers.entity.Order

interface OrderService {
    @POST("order")
    suspend fun all(
        @Body phoneNumber: String
    ): Response<List<Order>>

    @PUT("order")
    suspend fun create(
        @Body orderCreationRequestDTO: OrderCreationRequestDTO
    ): Response<Order>


}