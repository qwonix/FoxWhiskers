package ru.qwonix.android.foxwhiskers.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentOrderBinding
import ru.qwonix.android.foxwhiskers.fragment.adapter.OrderDishAdapter
import ru.qwonix.android.foxwhiskers.viewmodel.MenuViewModel


class OrderFragment : Fragment(R.layout.fragment_order) {

    private lateinit var binding: FragmentOrderBinding
    private val menuViewModel: MenuViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderDishAdapter = OrderDishAdapter(viewLifecycleOwner)
//        orderDishAdapter.setData(
//            listOf(
//                Dish(
//                    1,
//                    "Пицца",
//                    "https://i.imgur.com/dNpAg7f.jpg",
//                    "целая, 42 см, 1350 гр",
//                    "2131.32 ₽",
//                    2
//                ),
//                Dish(
//                    1,
//                    "Пицца",
//                    "https://i.imgur.com/H1ieAcE.png",
//                    "целая, 42 см, 1350 гр",
//                    "123 ₽"
//                ),
//                Dish(
//                    1,
//                    "Пицца",
//                    "https://i.imgur.com/H1ieAcE.png",
//                    "целая, 42 см, 1350 гр",
//                    "669 ₽"
//                ),
//                Dish(
//                    1,
//                    "Пицца",
//                    "https://i.imgur.com/kzUwGbe.jpg",
//                    "целая, 42 см, 1350 гр",
//                    "842 ₽"
//                )
//            )
//        )
        menuViewModel.dishTypeDishMap.observe(viewLifecycleOwner) {
            orderDishAdapter.setData(it.values.flatten().filter { dish -> dish.count > 0 })
        }

        binding.recyclerOrderedDishes.apply {
            adapter = orderDishAdapter
            val manager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            layoutManager = manager
        }
        // TODO: loading progress dialog
//        viewModel.loading.observe(this, Observer {
//            if (it) {
//                binding.progressDialog.visibility = View.VISIBLE
//            } else {
//                binding.progressDialog.visibility = View.GONE
//            }
//        })
    }
}