package ru.qwonix.android.foxwhiskers.fragment

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentCartBinding
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.fragment.adapter.CartDishAdapter
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.util.OrderConfirmationBottomSheetDialogFragment
import ru.qwonix.android.foxwhiskers.util.Utils
import ru.qwonix.android.foxwhiskers.viewmodel.MenuViewModel
import java.math.BigDecimal


class CartFragment : Fragment(R.layout.fragment_cart) {

    private val TAG = "CartFragment"

    private lateinit var binding: FragmentCartBinding
    private val menuViewModel: MenuViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            priceFormat = Utils.DECIMAL_FORMAT
            orderPrice = 0.0
            orderItemCount = 0
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cartDishAdapter = CartDishAdapter()

        menuViewModel.dishes.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "code: ${it.code} – ${it.errorMessage}")
                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")


                is ApiResponse.Success -> {
                    Log.i(TAG, "Successful load menu ${it.data}")
                    val cartDishes = it.data.filter { it.count > 0 }.toMutableList()

                    cartDishes.forEach {
                        it.addOnPropertyChangedCallback(object :
                            Observable.OnPropertyChangedCallback() {
                            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                val dish = sender as Dish

                                if (dish.count == 1 && dish !in cartDishes) {
                                    cartDishes.add(sender)
                                } else if ((sender.count == 0)) {
                                    cartDishes.remove(sender)
                                }

                                cartDishAdapter.setOrderDishes(cartDishes)

                                binding.orderPrice = (cartDishes.sumOf { dish ->
                                    BigDecimal(dish.currencyPrice).multiply(
                                        (BigDecimal(
                                            dish.count
                                        ))
                                    )
                                }).toDouble()
                                binding.orderItemCount = cartDishes.sumOf { dish -> dish.count }

                            }
                        })
                    }
                    cartDishAdapter.setOrderDishes(cartDishes)
                    binding.orderPrice =
                        (cartDishes.sumOf { dish ->
                            BigDecimal(dish.currencyPrice).multiply(
                                (BigDecimal(
                                    dish.count
                                ))
                            )
                        }).toDouble()
                    binding.orderItemCount = cartDishes.sumOf { dish -> dish.count }
                }
            }

        }

        binding.recyclerOrderedDishes.apply {
            adapter = cartDishAdapter
            val manager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            layoutManager = manager

            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.bottom = 25
                }
            })
        }

        binding.currentOrderButton.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_orderReceiptFragment)
        }

        binding.goToMenuButton.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_menuFragment)
        }


        binding.checkoutButton.setOnClickListener {
            OrderConfirmationBottomSheetDialogFragment(OrderConfirmationFragment.newInstance()).show(
                parentFragmentManager,
                "tag"
            )
        }

    }
}