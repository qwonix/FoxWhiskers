package ru.qwonix.android.foxwhiskers.entity

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ru.qwonix.android.foxwhiskers.BR

class Dish(
    var id: Long,
    var title: String,
    var imageUrl: String,
    var shortDescription: String,
    var currencyPriceText: String,
    var currencyPrice: Double,
    var type : DishType
) : BaseObservable() {

    @Bindable
    var count: Int = 0
        set(value) {
            field = value
            notifyPropertyChanged(BR.count)
        }
}
