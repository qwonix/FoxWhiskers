package ru.qwonix.android.foxwhiskers.fragment.adapter

import ru.qwonix.android.foxwhiskers.entity.Dish

interface DishCountChangeListener {
    fun beforeCountChange(dish: Dish, newCount: Int)
}