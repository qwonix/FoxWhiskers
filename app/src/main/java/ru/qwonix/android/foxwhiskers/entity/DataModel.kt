package ru.qwonix.android.foxwhiskers.entity

sealed class DataModel {
    data class Dish(
        var title: String,
        var imageUrl: String,
        var shortDescription: String,
        var currencyPrice: String,
        var type: String
    ) : DataModel()

    data class DishType(
        val title: String,
    ) : DataModel()
}