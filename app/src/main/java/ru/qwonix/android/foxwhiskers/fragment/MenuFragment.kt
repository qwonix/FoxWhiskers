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
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.squareup.picasso.Picasso
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentMenuBinding
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.entity.DishType
import ru.qwonix.android.foxwhiskers.fragment.adapter.MenuDishAdapter
import ru.qwonix.android.foxwhiskers.fragment.adapter.MenuDishTypeChipAdapter
import ru.qwonix.android.foxwhiskers.util.DemoBottomSheetDialogFragment
import ru.qwonix.android.foxwhiskers.viewmodel.MenuViewModel


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

        val menuDishAdapter = MenuDishAdapter()
        val menuDishTypeChipAdapter = MenuDishTypeChipAdapter(binding.recyclerDishes)

        menuViewModel.dishes.observe(viewLifecycleOwner) {
            menuDishAdapter.setDishes(it)
        }

        menuViewModel.dishes.observe(viewLifecycleOwner) {
            val dishes: Map<DishType, List<Dish>> = it.groupBy { dish: Dish -> dish.type }

            val chips: MutableMap<DishType, Int> =
                mutableMapOf()
            var i = 0
            for ((k, v) in dishes) {
                chips.put(k, i)
                i += v.count() + 1
            }

            menuDishTypeChipAdapter.onItemClickListener =
                object : MenuDishTypeChipAdapter.OnItemClickListener {
                    override fun onItemClick(recyclerView: RecyclerView, position: Int) {
                        val smoothScroller: SmoothScroller =
                            object : LinearSmoothScroller(context) {
                                override fun getVerticalSnapPreference(): Int {
                                    return SNAP_TO_START
                                }
                            }
                        smoothScroller.targetPosition = position
                        recyclerView.layoutManager?.startSmoothScroll(smoothScroller)
                    }
                }

            menuDishTypeChipAdapter.setDishTypes(chips)
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

        binding.searchBar.setOnClickListener {
            DemoBottomSheetDialogFragment(MenuSearchFragment.newInstance()).show(
                parentFragmentManager,
                "tag"
            )
        }

    }
}