package ru.qwonix.android.foxwhiskers.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.qwonix.android.foxwhiskers.databinding.ItemMenuDishTypeChipBinding
import ru.qwonix.android.foxwhiskers.entity.DishType


class MenuDishTypeChipAdapter(val recyclerView: RecyclerView) : RecyclerView.Adapter<MenuDishTypeChipAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(recyclerView: RecyclerView, position: Int)
    }

    lateinit var onItemClickListener: OnItemClickListener
    set

    var dishTypes = emptyList<Pair<DishType, Int>>()

    fun setDishTypes(dishTypeOnClick: Map<DishType, Int>) {
        dishTypes = dishTypeOnClick.toList()
    }

    private lateinit var binding: ItemMenuDishTypeChipBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            ItemMenuDishTypeChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pair = dishTypes[position]
        holder.bind(pair.first)
    }

    override fun getItemCount(): Int = dishTypes.size

    inner class ViewHolder(
        private val binding: ItemMenuDishTypeChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClickListener.onItemClick(recyclerView, dishTypes[adapterPosition].second)
            }
        }

        fun bind(dishType: DishType) {
            binding.dishType = dishType
            binding.isChecked = true
        }
    }
}


