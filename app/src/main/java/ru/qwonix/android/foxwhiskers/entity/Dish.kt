package ru.qwonix.android.foxwhiskers.entity

data class Dish(
    var title: String,
    var imageUrl: String,
    var shortDescription: String,
    var currencyPrice: String,
    var type: DishType
)
