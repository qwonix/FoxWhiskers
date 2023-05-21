package ru.qwonix.android.foxwhiskers.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.qwonix.android.foxwhiskers.dao.MenuItem
import ru.qwonix.android.foxwhiskers.databinding.ItemMenuDishBinding
import ru.qwonix.android.foxwhiskers.databinding.ItemMenuDishTypeBinding
import ru.qwonix.android.foxwhiskers.util.Utils


class MenuDishAdapter(
    private val dishCountChangeListener: DishCountChangeListener
) : RecyclerView.Adapter<MenuDishAdapter.ViewHolder>() {

    private val data: MutableList<DataModel> = mutableListOf()

    fun setDishes(menuItems: List<MenuItem>) {
        val dishesAdapterDataModels = mutableListOf<DataModel>()

        for ((title, dishes) in menuItems) {
            dishesAdapterDataModels.add(DataModel.Title(title))
            dishesAdapterDataModels.addAll(dishes.map { DataModel.Dish(it) })
        }

        val menuDishesDiffUtil = MenuDishesDiffUtil(data, dishesAdapterDataModels)
        val diffResult = DiffUtil.calculateDiff(menuDishesDiffUtil)
        diffResult.dispatchUpdatesTo(this)

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
            is DataModel.Title -> TYPE_DISH_TYPE
        }
    }

    inner class ViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private fun bindDish(dish: DataModel.Dish) {
            (binding as ItemMenuDishBinding).dish = dish.value
            binding.priceFormat = Utils.DECIMAL_FORMAT
            binding.dishCountChangeListener = dishCountChangeListener
        }

        private fun bindDishType(title: DataModel.Title) {
            (binding as ItemMenuDishTypeBinding).title = title.value
        }

        fun bind(dataModel: DataModel) {
            when (dataModel) {
                is DataModel.Dish -> bindDish(dataModel)
                is DataModel.Title -> bindDishType(dataModel)
            }
        }
    }

    sealed class DataModel {
        data class Dish(
            var value: ru.qwonix.android.foxwhiskers.entity.Dish
        ) : DataModel()

        data class Title(
            val value: String
        ) : DataModel()
    }

    private class MenuDishesDiffUtil(
        private val oldList: List<DataModel>,
        private val newList: List<DataModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            if (oldItem is DataModel.Dish && newItem is DataModel.Dish) {
                return oldItem.value.id == newItem.value.id
            }
            if (oldItem is DataModel.Title && newItem is DataModel.Title) {
                return oldItem.value == newItem.value
            }
            return false
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]

            if (oldItem is DataModel.Title && newItem is DataModel.Title) {
                return oldItem.value == newItem.value
            }
            if (oldItem is DataModel.Dish && newItem is DataModel.Dish) {
                return oldItem.value.id == newItem.value.id && oldItem.value.count == newItem.value.count
            }

            return false
        }
    }
}
