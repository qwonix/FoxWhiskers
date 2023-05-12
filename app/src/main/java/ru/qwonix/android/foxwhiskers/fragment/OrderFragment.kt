package ru.qwonix.android.foxwhiskers.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentOrderBinding
import ru.qwonix.android.foxwhiskers.fragment.adapter.OrderDishAdapter
import ru.qwonix.android.foxwhiskers.util.DemoBottomSheetDialogFragment
import ru.qwonix.android.foxwhiskers.util.Utils
import ru.qwonix.android.foxwhiskers.viewmodel.MenuViewModel
import java.math.BigDecimal


class OrderFragment : Fragment(R.layout.fragment_order) {

    private lateinit var binding: FragmentOrderBinding
    private val menuViewModel: MenuViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
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

        val orderDishAdapter = OrderDishAdapter()
        menuViewModel.orderCart.observe(viewLifecycleOwner) {
            orderDishAdapter.data = it
            binding.orderPrice =
                (it.sumOf { dish -> BigDecimal(dish.currencyPrice).multiply((BigDecimal(dish.count))) }).toDouble()
            binding.orderItemCount = it.count()
        }

        binding.recyclerOrderedDishes.apply {
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
                    outRect.bottom = 25
                }
            })
        }


        binding.checkoutButton.setOnClickListener {
            DemoBottomSheetDialogFragment(OrderConfirmationFragment.newInstance()).show(
                parentFragmentManager,
                "tag"
            )
        }

    }
}