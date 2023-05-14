package ru.qwonix.android.foxwhiskers.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import ru.qwonix.android.foxwhiskers.databinding.ItemMenuDishBinding
import ru.qwonix.android.foxwhiskers.databinding.ItemMenuDishTypeBinding
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.entity.DishType
import ru.qwonix.android.foxwhiskers.util.Utils


class MenuDishAdapter : RecyclerView.Adapter<MenuDishAdapter.ViewHolder>() {

    private val data: MutableList<DataModel> = mutableListOf()

    fun setDishes(dishes: List<Dish>) {
        val groupedDishes: Map<DishType, List<Dish>> = dishes.groupBy { dish: Dish -> dish.type }
        val dishesAdapterDataModels = mutableListOf<DataModel>()

        for ((dishType, dishesByType) in groupedDishes) {
            dishesAdapterDataModels.add(DataModel.DishType(dishType))
            dishesAdapterDataModels.addAll(dishesByType.map { DataModel.Dish(it) })
        }

        data.clear()
        data.addAll(dishesAdapterDataModels)
    }


    companion object {
        const val TYPE_DISH = 0
        const val TYPE_DISH_TYPE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = when (viewType) {
            TYPE_DISH -> ItemMenuDishBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            TYPE_DISH_TYPE -> ItemMenuDishTypeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is DataModel.Dish -> TYPE_DISH
            is DataModel.DishType -> TYPE_DISH_TYPE
        }
    }

    class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        private fun bindDish(dish: DataModel.Dish) {
            (binding as ItemMenuDishBinding).dish = dish.value
            binding.priceFormat = Utils.DECIMAL_FORMAT
        }

        private fun bindDishType(dishType: DataModel.DishType) {
            (binding as ItemMenuDishTypeBinding).dishType = dishType.value
        }

        fun bind(dataModel: DataModel) {
            when (dataModel) {
                is DataModel.Dish -> bindDish(dataModel)
                is DataModel.DishType -> bindDishType(dataModel)
            }
        }
    }

    sealed class DataModel {
        data class Dish(
            var value: ru.qwonix.android.foxwhiskers.entity.Dish
        ) : DataModel()

        data class DishType(
            val value: ru.qwonix.android.foxwhiskers.entity.DishType
        ) : DataModel()
    }
}
