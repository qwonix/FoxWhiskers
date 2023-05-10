package ru.qwonix.android.foxwhiskers.service

import retrofit2.Response
import retrofit2.http.GET
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.entity.PickUpLocation

interface MenuService {

    @GET("dish")
    suspend fun findAllDishes(): Response<List<Dish>>

    @GET("location")
    suspend fun findAllLocations(): Response<List<PickUpLocation>>

}