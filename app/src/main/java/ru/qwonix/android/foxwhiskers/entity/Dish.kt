package ru.qwonix.android.foxwhiskers.entity

import androidx.lifecycle.MutableLiveData

data class Dish(
    var id: Long,
    var title: String,
    var imageUrl: String,
    var shortDescription: String,
    var currencyPrice: String,
    var count: MutableLiveData<Int> = MutableLiveData(0)
) {
    fun setCount(value: Int) {
        count.postValue(value)
    }
}
