package ru.qwonix.android.foxwhiskers.entity

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ru.qwonix.android.foxwhiskers.BR

class Dish(
    val id: Long,
    val title: String,
    val imageUrl: String,
    val shortDescription: String,
    val currencyPrice: Double,
    val type: DishType
) : BaseObservable() {

    @Bindable
    var count: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.count)
        }
}
