package ru.qwonix.android.foxwhiskers.service

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import ru.qwonix.android.foxwhiskers.entity.PickUpLocation

interface PickUpLocationService {

    @GET("location")
    suspend fun findAll(): Response<List<PickUpLocation>>

    @GET("location/{id}")
    suspend fun findById(@Path("id") id: Long): Response<PickUpLocation>

    @GET("location?priority=max")
    suspend fun findMaxPriority(): Response<PickUpLocation>

}