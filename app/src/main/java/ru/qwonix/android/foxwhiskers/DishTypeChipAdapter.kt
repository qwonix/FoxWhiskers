package ru.qwonix.android.foxwhiskers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.qwonix.android.foxwhiskers.databinding.ItemDishTypeChipBinding

class DishType(
    var title: String
)

class DishTypeChipAdapter : RecyclerView.Adapter<DishTypeChipAdapter.ViewHolder>() {

    var dishTypes = listOf<DishType>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private lateinit var binding: ItemDishTypeChipBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            ItemDishTypeChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dishTypes[position])
    }

    override fun getItemCount(): Int = dishTypes.size

    class ViewHolder(
        private val binding: ItemDishTypeChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dishType: DishType) {
            binding.dishType = dishType
        }
    }
}


