package ru.qwonix.android.foxwhiskers.service

import retrofit2.Response
import retrofit2.http.GET
import ru.qwonix.android.foxwhiskers.entity.PaymentMethod

interface PaymentMethodService {

    @GET("payment/method?priority=max")
    suspend fun findMaxPriority(): Response<PaymentMethod>


}