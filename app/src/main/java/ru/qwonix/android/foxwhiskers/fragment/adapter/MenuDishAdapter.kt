package ru.qwonix.android.foxwhiskers.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import ru.qwonix.android.foxwhiskers.databinding.ItemMenuDishBinding
import ru.qwonix.android.foxwhiskers.databinding.ItemMenuDishTypeBinding
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.entity.DishType


class MenuDishAdapter : RecyclerView.Adapter<MenuDishAdapter.ViewHolder>() {

    private val dishes: MutableList<DataModel> = mutableListOf()

    private fun parseData(data: Map<DishType, List<Dish>>): ArrayList<DataModel> {
        val recyclerDishesAdapterDataModels = ArrayList<DataModel>()
        for ((dishType, dishes) in data) {
            recyclerDishesAdapterDataModels.add(DataModel.DishType(dishType))
            for (dish in dishes) {
                recyclerDishesAdapterDataModels.add(DataModel.Dish(dish))
            }
        }
        return recyclerDishesAdapterDataModels
    }

    fun setDishes(dishes: Map<DishType, List<Dish>>) {
        this.dishes.apply {
            clear()
            addAll(parseData(dishes))
        }
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
        holder.bind(dishes[position])
    }

    override fun getItemCount(): Int = dishes.size

    override fun getItemViewType(position: Int): Int {
        return when (dishes[position]) {
            is DataModel.Dish -> TYPE_DISH
            is DataModel.DishType -> TYPE_DISH_TYPE
        }
    }

    class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        private fun bindDish(dish: DataModel.Dish) {
            (binding as ItemMenuDishBinding).dish = dish.value
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
