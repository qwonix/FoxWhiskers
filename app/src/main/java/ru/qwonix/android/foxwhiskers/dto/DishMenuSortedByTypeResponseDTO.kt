package ru.qwonix.android.foxwhiskers.dto

import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.entity.DishType

data class DishMenuSortedByTypeResponseDTO(
    var dishType: DishType, var dishes: List<Dish>
)
