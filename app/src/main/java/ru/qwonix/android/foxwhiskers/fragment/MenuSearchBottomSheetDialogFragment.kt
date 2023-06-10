package ru.qwonix.android.foxwhiskers.fragment

import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentMenuSearchBinding
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.fragment.adapter.DishCountChangeListener
import ru.qwonix.android.foxwhiskers.fragment.adapter.MenuSearchDishAdapter
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.util.focusAndShowKeyboard
import ru.qwonix.android.foxwhiskers.util.onSearch
import ru.qwonix.android.foxwhiskers.viewmodel.CartViewModel
import ru.qwonix.android.foxwhiskers.viewmodel.CoroutinesErrorHandler
import ru.qwonix.android.foxwhiskers.viewmodel.MenuViewModel


@AndroidEntryPoint
class MenuSearchBottomSheetDialogFragment :
    BottomSheetDialogFragment(R.layout.fragment_menu_search) {

    private val TAG = "MenuSearchBottomSheet"

    private lateinit var binding: FragmentMenuSearchBinding
    private val menuViewModel: MenuViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState)
            .also { dialog ->
                dialog.setOnShowListener {
                    val viewBehavior = BottomSheetBehavior.from(
                        dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet)
                    )
                    setupBehavior(viewBehavior)
                }
            }
    }

    private fun setupBehavior(bottomSheetBehavior: BottomSheetBehavior<View>) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.isDraggable = false
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuSearchBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.goBackButton.setOnClickListener {
            dismiss()
        }

        val orderDishAdapter = MenuSearchDishAdapter(object : DishCountChangeListener {
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

        binding.recyclerSearchedDishes.apply {
            adapter = orderDishAdapter
            val manager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            layoutManager = manager

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.bottom = 15
                }
            })
        }

        binding.searchBarEditText.addTextChangedListener {
            if (it.isNullOrBlank()) {
                orderDishAdapter.setFoundedDishes(emptyList())
                binding.clearTextButton.visibility = View.INVISIBLE
            } else {
                when (val dishes = menuViewModel.menu.value) {
                    is ApiResponse.Failure -> {
                        Log.e(TAG, "code: ${dishes.code} – ${dishes.errorMessage}")
                    }

                    is ApiResponse.Loading -> Log.i(TAG, "loading")

                    is ApiResponse.Success -> {
                        Log.i(TAG, "Successful load dishes ${dishes.data}")
                        binding.clearTextButton.visibility = View.VISIBLE

                        val foundDishes =
                            dishes.data
                                .map { menuItem -> menuItem.items }
                                .flatten()
                                .filter { dish ->
                                    dish.title.contains(it, true)
                                }

                        orderDishAdapter.setFoundedDishes(foundDishes)
                    }

                    else -> {
                        TODO("Not yet implemented")
                    }
                }
            }
        }

        binding.searchBarEditText.focusAndShowKeyboard()
        binding.searchBarEditText.onSearch { }

        menuViewModel.loadDishes(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented $message")
            }
        })
    }
}