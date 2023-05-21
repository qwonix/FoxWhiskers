package ru.qwonix.android.foxwhiskers.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentOrderConfirmationBinding
import ru.qwonix.android.foxwhiskers.entity.PaymentMethod
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.util.Utils
import ru.qwonix.android.foxwhiskers.util.withDemoBottomSheet
import ru.qwonix.android.foxwhiskers.viewmodel.CartViewModel
import ru.qwonix.android.foxwhiskers.viewmodel.CoroutinesErrorHandler
import ru.qwonix.android.foxwhiskers.viewmodel.OrderViewModel
import ru.qwonix.android.foxwhiskers.viewmodel.ProfileViewModel

@AndroidEntryPoint
class OrderConfirmationFragment : Fragment(R.layout.fragment_order_confirmation) {

    private val TAG = "OrderConfirmFragment"

    companion object {
        fun newInstance() = OrderConfirmationFragment()
    }

    private lateinit var binding: FragmentOrderConfirmationBinding
    private val cartViewModel: CartViewModel by viewModels()
    private val orderViewModel: OrderViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderConfirmationBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            priceFormat = Utils.DECIMAL_FORMAT
            orderPrice = cartViewModel.cartTotalPrice.value
            paymentMethod = PaymentMethod.CASH
            pickUpLocationTitle = "menuViewModel.selectedPickUpLocation.value!!.title"
            pickUpLocationDescription = "menuViewModel.selectedPickUpLocation.value!!.description"
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.optionPickupLocation.setOnClickListener {
            withDemoBottomSheet { goToPickUpLocationFragment() }
        }

        binding.optionPaymentMethod.setOnClickListener {
            withDemoBottomSheet { goToOrderConfirmationPaymentFragment() }
        }

        binding.checkoutOrderButton.setOnClickListener {
            orderViewModel.createOrder(
                (profileViewModel.clientAuthenticationResponse.value as ApiResponse.Success).data!!.phoneNumber,
                cartViewModel.getDishesInCart(),
                1,
                PaymentMethod.INAPP_ONLINE_CARD,
                object : CoroutinesErrorHandler {
                    override fun onError(message: String) {
                        TODO("Not yet implemented")
                    }
                }
            )
        }

        orderViewModel.orderCreationRequest.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "code: ${it.code} – ${it.errorMessage}")
                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")


                is ApiResponse.Success -> {
                    Log.i(TAG, "Successful create order ${it.data}")
                    findNavController().navigate(R.id.orderReceiptFragment)
                }
            }
        }
    }
}
