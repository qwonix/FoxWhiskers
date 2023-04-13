package ru.qwonix.android.foxwhiskers.fragment

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
import ru.qwonix.android.foxwhiskers.viewmodel.MenuViewModel


class OrderFragment : Fragment(R.layout.fragment_order) {

    private lateinit var binding: FragmentOrderBinding
    private val menuViewModel: MenuViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        binding.apply {
            viewModel = menuViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val value = menuViewModel.dishes.value ?: emptyList()
        val data = value.filter { dish -> dish.count > 0 }.toMutableList()

        val orderDishAdapter = OrderDishAdapter()
        orderDishAdapter.setData(data)

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