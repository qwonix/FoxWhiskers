package ru.qwonix.android.foxwhiskers.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.qwonix.android.foxwhiskers.databinding.ItemMenuDishTypeChipBinding


class MenuDishTypeChipAdapter(private val recyclerView: RecyclerView) :
    RecyclerView.Adapter<MenuDishTypeChipAdapter.ViewHolder>() {

    val data = mutableListOf<Pair<String, Int>>()

    fun setDishTypes(dishTypeOnClick: List<Pair<String, Int>>) {
        data.clear()
        data.addAll(dishTypeOnClick)
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(recyclerView: RecyclerView, position: Int)
    }

    lateinit var onItemClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMenuDishTypeChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pair = data[position]
        holder.bind(pair.first)
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(
        private val binding: ItemMenuDishTypeChipBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                onItemClickListener.onItemClick(recyclerView, data[adapterPosition].second)
            }
        }

        fun bind(title: String) {
            binding.title = title
            binding.isChecked = true
        }
    }
}


