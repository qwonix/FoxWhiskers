package ru.qwonix.android.foxwhiskers.fragment

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentMenuSearchBinding
import ru.qwonix.android.foxwhiskers.fragment.adapter.MenuSearchDishAdapter
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.util.focusAndShowKeyboard
import ru.qwonix.android.foxwhiskers.util.onSearch
import ru.qwonix.android.foxwhiskers.util.withDemoBottomSheet
import ru.qwonix.android.foxwhiskers.viewmodel.MenuViewModel


class MenuSearchFragment : Fragment(R.layout.fragment_menu_search) {

    private val TAG = "MenuSearchFragment"

    companion object {
        fun newInstance() = MenuSearchFragment()
    }

    private lateinit var binding: FragmentMenuSearchBinding
    private val menuViewModel: MenuViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backArrow.setOnClickListener {
            withDemoBottomSheet { dismiss() }
        }

        val orderDishAdapter = MenuSearchDishAdapter()

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

        binding.searchBarTextView.addTextChangedListener {
            if (it.isNullOrBlank()) {
                orderDishAdapter.setFoundDishes(emptyList())
                binding.clearText.visibility = View.INVISIBLE
            } else {
                when (val dishes = menuViewModel.dishes.value) {
                    is ApiResponse.Failure -> {
                        Log.e(TAG, "code: ${dishes.code} – ${dishes.errorMessage}")
                    }

                    is ApiResponse.Loading -> Log.i(TAG, "loading")

                    is ApiResponse.Success -> {
                        Log.i(TAG, "Successful load dishes ${dishes.data}")
                        binding.clearText.visibility = View.VISIBLE

                        val foundDishes = dishes.data.filter { dish ->
                            dish.title.contains(it, true)
                        }

                        orderDishAdapter.setFoundDishes(foundDishes)
                    }

                    else -> {
                        TODO("Not yet implemented")
                    }
                }
            }
        }

        binding.searchBarTextView.focusAndShowKeyboard()
        binding.searchBarTextView.onSearch { }
    }
}