package ru.qwonix.android.foxwhiskers.fragment

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentMenuBinding
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.fragment.adapter.DishCountChangeListener
import ru.qwonix.android.foxwhiskers.fragment.adapter.MenuDishAdapter
import ru.qwonix.android.foxwhiskers.fragment.adapter.MenuDishTypeChipAdapter
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.viewmodel.CartViewModel
import ru.qwonix.android.foxwhiskers.viewmodel.CoroutinesErrorHandler
import ru.qwonix.android.foxwhiskers.viewmodel.MenuViewModel


@AndroidEntryPoint
class MenuFragment : Fragment(R.layout.fragment_menu) {

    private val TAG = "MenuFragment"

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


    private val onChipClickListener = object : MenuDishTypeChipAdapter.OnItemClickListener {
        override fun onItemClick(recyclerView: RecyclerView, position: Int) {
            val smoothScroller: RecyclerView.SmoothScroller =
                object : LinearSmoothScroller(context) {
                    override fun getVerticalSnapPreference(): Int {
                        return SNAP_TO_START
                    }
                }
            smoothScroller.targetPosition = position
            recyclerView.layoutManager?.startSmoothScroll(smoothScroller)
        }
    }

    private lateinit var binding: FragmentMenuBinding
    private val menuViewModel: MenuViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        binding.isLoading = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuDishAdapter = MenuDishAdapter(object : DishCountChangeListener {
            override fun beforeCountChange(dish: Dish, newCount: Int) {
                Log.i(TAG, "change count of \"$dish\" to $newCount")
                dish.count = newCount
                cartViewModel.changeDishCount(dish, newCount, object : CoroutinesErrorHandler {
                    override fun onError(message: String) {
                        TODO("Not yet implemented")
                    }
                })
            }
        })

        val menuDishTypeChipAdapter = MenuDishTypeChipAdapter(binding.recyclerDishes)
        menuDishTypeChipAdapter.onItemClickListener = onChipClickListener

        menuViewModel.menu.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "code: ${it.code} – ${it.errorMessage}")
                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")


                is ApiResponse.Success -> {
                    Log.i(TAG, "Successful load dishes ${it.data}")
                    menuDishAdapter.setMenuDishes(it.data)

                    val chips = mutableListOf<Pair<String, Int>>()
                    var i = 0
                    for ((k, v) in it.data) {
                        chips.add(Pair(k, i))
                        i += v.size + 1
                    }
                    menuDishTypeChipAdapter.setMenuDishTypes(chips)
                    binding.isLoading = false
                }
            }
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
            MenuSearchBottomSheetDialogFragment().show(
                parentFragmentManager,
                "tag"
            )
        }

        Log.i(TAG, "menuViewModel.loadDishes from menu")
        menuViewModel.loadDishes(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented $message")
            }
        })
    }
}