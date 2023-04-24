package ru.qwonix.android.foxwhiskers.repository

import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.entity.PickUpLocation
import ru.qwonix.android.foxwhiskers.entity.UserProfile

interface FoxWhiskersRepository {
    suspend fun findAllDishes(): ResponseDao<List<Dish>>
    suspend fun findAllLocations(): ResponseDao<List<PickUpLocation>>

    suspend fun findUserProfile(): ResponseDao<UserProfile>

}