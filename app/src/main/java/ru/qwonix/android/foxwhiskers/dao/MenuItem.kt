package ru.qwonix.android.foxwhiskers.dao

import ru.qwonix.android.foxwhiskers.entity.Dish

data class MenuItem(
    val title: String,
    val items: List<Dish>
)