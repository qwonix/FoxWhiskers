package ru.qwonix.android.foxwhiskers.fragment

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentCartBinding
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.fragment.adapter.CartDishAdapter
import ru.qwonix.android.foxwhiskers.fragment.adapter.DishCountChangeListener
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.util.OrderConfirmationBottomSheetDialogFragment
import ru.qwonix.android.foxwhiskers.util.Utils
import ru.qwonix.android.foxwhiskers.viewmodel.CartViewModel
import ru.qwonix.android.foxwhiskers.viewmodel.CoroutinesErrorHandler

@AndroidEntryPoint
class CartFragment : Fragment(R.layout.fragment_cart) {

    private val TAG = "CartFragment"

    private lateinit var binding: FragmentCartBinding
    private val cartViewModel: CartViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            priceFormat = Utils.DECIMAL_FORMAT
            orderPrice = cartViewModel.cartTotalPrice.value
            orderItemCount = cartViewModel.cartTotalCount.value
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartViewModel.cartTotalPrice.observe(viewLifecycleOwner) {
            binding.orderPrice = it
        }
        cartViewModel.cartTotalCount.observe(viewLifecycleOwner) {
            binding.orderItemCount = it
        }

        val cartDishAdapter = CartDishAdapter(object : DishCountChangeListener {
            override fun beforeCountChange(dish: Dish, newCount: Int) {
                dish.count = newCount

                cartViewModel.changeDishCount(dish, newCount, object : CoroutinesErrorHandler {
                    override fun onError(message: String) {
                        TODO("Not yet implemented")
                    }
                })
            }
        })

        cartViewModel.cart.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "code: ${it.code} – ${it.errorMessage}")
                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")


                is ApiResponse.Success -> {
                    Log.i(TAG, "Successful load dishes in cart ${it.data}")
                    cartDishAdapter.setCartDishes(it.data.toList())
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

        Log.i(TAG, "cartViewModel.load from cart")
        cartViewModel.load(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented $message")
            }
        })
    }
}