package ru.qwonix.android.foxwhiskers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.qwonix.android.foxwhiskers.databinding.ItemDishBinding
import ru.qwonix.android.foxwhiskers.entity.Dish


class DishAdapter : RecyclerView.Adapter<DishAdapter.ViewHolder>() {

    var dishes = listOf<Dish>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private lateinit var binding: ItemDishBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            ItemDishBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dishes[position])
    }

    override fun getItemCount(): Int = dishes.size

    class ViewHolder(
        private val binding: ItemDishBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dish: Dish) {
            binding.dish = dish
        }
    }
}
