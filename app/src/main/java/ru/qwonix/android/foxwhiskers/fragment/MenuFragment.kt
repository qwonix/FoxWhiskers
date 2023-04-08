package ru.qwonix.android.foxwhiskers.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.qwonix.android.foxwhiskers.viewmodel.MenuViewModel
import ru.qwonix.android.foxwhiskers.fragment.adapter.MenuDishAdapter
import ru.qwonix.android.foxwhiskers.fragment.adapter.MenuDishTypeChipAdapter
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentMenuBinding


class MenuFragment : Fragment(R.layout.fragment_menu) {
    companion object {
        /**
         * Load picture from the internet via Picasso
         * @see Picasso
         */
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, imageUrl: String?) {
            if (imageUrl != null) {
                if (imageUrl.isNotBlank()) {
                    Picasso.get()
                        .load(imageUrl)
//                        .placeholder(R.drawable.placeholder)
//                        .error(R.drawable.error)
                        .into(view)
                }
            } else {
//                view.setImageDrawable(R.drawable.placeholder)
            }
        }
    }

    private lateinit var binding: FragmentMenuBinding
    private val menuViewModel: MenuViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuDishAdapter = MenuDishAdapter(viewLifecycleOwner)
        val menuDishTypeChipAdapter = MenuDishTypeChipAdapter()
        menuViewModel.dishTypeDishMap.observe(viewLifecycleOwner) {
            val recyclerDishesAdapterDataModels = ArrayList<MenuDishAdapter.DataModel>()
            for ((dishType, dishes) in it) {
                recyclerDishesAdapterDataModels.add(MenuDishAdapter.DataModel.DishType(dishType))
                for (dish in dishes) {
                    recyclerDishesAdapterDataModels.add(MenuDishAdapter.DataModel.Dish(dish))
                }
            }
            menuDishAdapter.dishes = recyclerDishesAdapterDataModels
            menuDishTypeChipAdapter.dishTypes = it.keys.toList()
        }

        binding.recyclerDishTypeChip.apply {
            adapter = menuDishTypeChipAdapter
            val manager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            layoutManager = manager

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.right = 15
                }
            })
        }

        binding.recyclerDishes.apply {
            adapter = menuDishAdapter
            val manager = GridLayoutManager(context, 2)
            layoutManager = manager

            manager.spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return when (menuDishAdapter.getItemViewType(position)) {
                        MenuDishAdapter.TYPE_DISH -> 1
                        MenuDishAdapter.TYPE_DISH_TYPE -> manager.spanCount
                        else -> 0
                    }
                }
            }

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.bottom = 30
                    outRect.right = 30
                }
            })
        }

        menuViewModel.loadDishesMapByType()
    }
}