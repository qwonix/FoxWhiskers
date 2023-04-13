package ru.qwonix.android.foxwhiskers.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.recyclerview.widget.RecyclerView
import ru.qwonix.android.foxwhiskers.databinding.ItemOrderDishBinding
import ru.qwonix.android.foxwhiskers.entity.Dish


class OrderDishAdapter : RecyclerView.Adapter<OrderDishAdapter.ViewHolder>() {
    private var data = mutableListOf<Dish>()

    private val onDishCountChanged = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            if (((sender as Dish).count == 0)) {
                notifyItemRemoved(data.indexOf(sender))
                this@OrderDishAdapter.data.remove(sender)
            }
        }
    }

    fun setData(data: MutableList<Dish>) {
        data.map {
            it.addOnPropertyChangedCallback(onDishCountChanged)
        }
        this.data = data
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