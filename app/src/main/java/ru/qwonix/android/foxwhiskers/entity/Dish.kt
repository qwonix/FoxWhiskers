package ru.qwonix.android.foxwhiskers.entity

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import ru.qwonix.android.foxwhiskers.BR

data class Dish(
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Dish

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
