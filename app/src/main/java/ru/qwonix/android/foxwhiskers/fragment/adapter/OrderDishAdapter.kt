package ru.qwonix.android.foxwhiskers.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.qwonix.android.foxwhiskers.databinding.ItemOrderDishBinding
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.util.Utils


class OrderDishAdapter : RecyclerView.Adapter<OrderDishAdapter.ViewHolder>() {
    var data = emptyList<Dish>()
        set(value) {
            val orderDishesDiffUtil = OrderDishesDiffUtil(field, value)
            val diffResult = DiffUtil.calculateDiff(orderDishesDiffUtil)
            diffResult.dispatchUpdatesTo(this)

            field = value
        }

    private lateinit var binding: ItemOrderDishBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            ItemOrderDishBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(
        private val binding: ItemOrderDishBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dish: Dish) {
            binding.dish = dish
            binding.priceFormat = Utils.DECIMAL_FORMAT
        }
    }

    private class OrderDishesDiffUtil(
        private val oldList: List<Dish>,
        private val newList: List<Dish>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return when {
                oldList[oldItemPosition].id != newList[newItemPosition].id -> {
                    false
                }

                oldList[oldItemPosition].title != newList[newItemPosition].title -> {
                    false
                }

                oldList[oldItemPosition].count != newList[newItemPosition].count -> {
                    false
                }

                else -> {
                    true
                }
            }
        }

    }
}