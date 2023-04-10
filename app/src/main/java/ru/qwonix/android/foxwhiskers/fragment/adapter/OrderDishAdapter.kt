package ru.qwonix.android.foxwhiskers.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import ru.qwonix.android.foxwhiskers.databinding.ItemOrderDishBinding
import ru.qwonix.android.foxwhiskers.entity.Dish


class OrderDishAdapter : RecyclerView.Adapter<OrderDishAdapter.ViewHolder>() {
    private val data = mutableListOf<Dish>()

    fun setData(data: List<Dish>) {
        this.data.apply {
            clear()
            addAll(data)
        }
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
        }
    }
}