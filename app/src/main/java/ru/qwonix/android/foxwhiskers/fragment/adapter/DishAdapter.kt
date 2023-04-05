package ru.qwonix.android.foxwhiskers.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import ru.qwonix.android.foxwhiskers.databinding.ItemDishBinding
import ru.qwonix.android.foxwhiskers.databinding.ItemDishTypeBinding
import ru.qwonix.android.foxwhiskers.dto.DishMenuSortedByTypeResponseDTO


class DishAdapter : RecyclerView.Adapter<DishAdapter.ViewHolder>() {
    private val adapterData = mutableListOf<DataModel>()
    fun setData(data: List<DishMenuSortedByTypeResponseDTO>) {
        adapterData.apply {
            clear()
            for (menuResponseDTO in data) {
                add(DataModel.DishType(menuResponseDTO.dishType))
                for (dish in menuResponseDTO.dishes) {
                    add(DataModel.Dish(dish))
                }
            }
        }
    }

    companion object {
        const val TYPE_DISH = 0
        const val TYPE_DISH_TYPE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = when (viewType) {
            TYPE_DISH -> ItemDishBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            TYPE_DISH_TYPE -> ItemDishTypeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(adapterData[position])
    }

    override fun getItemCount(): Int = adapterData.size

    override fun getItemViewType(position: Int): Int {
        return when (adapterData[position]) {
            is DataModel.Dish -> TYPE_DISH
            is DataModel.DishType -> TYPE_DISH_TYPE
        }
    }

    class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        private fun bindDish(dish: DataModel.Dish) {
            (binding as ItemDishBinding).dish = dish.value
        }

        private fun bindDishType(dishType: DataModel.DishType) {
            (binding as ItemDishTypeBinding).dishType = dishType.value
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
