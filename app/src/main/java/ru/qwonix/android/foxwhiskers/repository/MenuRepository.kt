package ru.qwonix.android.foxwhiskers.repository

import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.entity.PickUpLocation

interface MenuRepository {
    suspend fun findAllDishes(): ResponseDao<List<Dish>>
    suspend fun findAllLocations(): ResponseDao<List<PickUpLocation>>
}