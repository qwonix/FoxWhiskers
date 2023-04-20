package ru.qwonix.android.foxwhiskers.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentOrderConfirmationBinding
import ru.qwonix.android.foxwhiskers.util.Utils
import ru.qwonix.android.foxwhiskers.util.withDemoBottomSheet
import ru.qwonix.android.foxwhiskers.viewmodel.MenuViewModel

class OrderConfirmationFragment : Fragment(R.layout.fragment_order_confirmation) {

    companion object {
        fun newInstance() = OrderConfirmationFragment()
    }

    private lateinit var binding: FragmentOrderConfirmationBinding
    private val menuViewModel: MenuViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderConfirmationBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = menuViewModel
            priceFormat = Utils.DECIMAL_FORMAT
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.optionPickupLocation.setOnClickListener {
            withDemoBottomSheet { goToFullScreenFragment() }
        }
    }
}
