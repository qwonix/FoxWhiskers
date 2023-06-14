package ru.qwonix.android.foxwhiskers.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import ru.qwonix.android.foxwhiskers.viewmodel.PaymentMethodViewModel
import ru.qwonix.android.foxwhiskers.viewmodel.PickUpLocationViewModel
import ru.qwonix.android.foxwhiskers.viewmodel.ProfileViewModel

@AndroidEntryPoint
class OrderConfirmationFragment : Fragment(R.layout.fragment_order_confirmation) {

    private val TAG = "OrderConfirmFragment"

    companion object {
        fun newInstance() = OrderConfirmationFragment()
    }

    private lateinit var binding: FragmentOrderConfirmationBinding

    private val cartViewModel: CartViewModel by activityViewModels()
    private val orderViewModel: OrderViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val pickUpLocationViewModel: PickUpLocationViewModel by activityViewModels()
    private val paymentMethodViewModel: PaymentMethodViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderConfirmationBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            priceFormat = Utils.DECIMAL_FORMAT
            orderPrice = cartViewModel.cartTotalPrice.value
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentMethodViewModel.selectedPaymentMethodResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "fail to load: ${it.code} – ${it.errorMessage}")

                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")

                is ApiResponse.Success -> {
                    binding.paymentMethod = it.data
                }
            }
        }

        pickUpLocationViewModel.selectedPickUpLocationResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "code: ${it.code} – ${it.errorMessage}")
                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")

                is ApiResponse.Success -> {
                    Log.i(TAG, "Successful load orders ${it.data}")
                    binding.pickUpLocation = it.data
                }
            }
        }

        binding.optionPickupLocation.setOnClickListener {
            withDemoBottomSheet { goToPickUpLocationFragment() }
        }

        binding.optionPaymentMethod.setOnClickListener {
            withDemoBottomSheet { goToOrderConfirmationPaymentFragment() }
        }

        binding.checkoutOrderButton.setOnClickListener {
            when (val clientResponse = profileViewModel.clientAuthenticationResponse.value) {
                is ApiResponse.Failure -> {
                    when (clientResponse.code) {
                        401 -> {
                            withDemoBottomSheet { dismiss() }
                            findNavController().navigate(R.id.action_cartFragment_to_profileNavigation)
                        }

                        400 -> {
                            withDemoBottomSheet { dismiss() }
                            findNavController().navigate(R.id.action_cartFragment_to_profileNavigation)
                        }
                    }
                    Log.e(TAG, "code: ${clientResponse.code} – ${clientResponse.errorMessage}")
                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")


                is ApiResponse.Success -> {
                    Log.i(TAG, "Successful load dishes in cart ${clientResponse.data}")
                    val client = clientResponse.data!!

                    when (val selectedPickUpLocation =
                        pickUpLocationViewModel.selectedPickUpLocationResponse.value) {
                        is ApiResponse.Failure -> {}
                        is ApiResponse.Loading -> Log.i(TAG, "loading")
                        is ApiResponse.Success -> {
                            val pickUpLocation = selectedPickUpLocation.data!!

                            orderViewModel.createOrder(
                                client.phoneNumber,
                                cartViewModel.getDishesInCart(),
                                pickUpLocation.id,
                                PaymentMethod.INAPP_ONLINE_CARD,
                                object : CoroutinesErrorHandler {
                                    override fun onError(message: String) {
                                        withDemoBottomSheet { dismiss() }
                                        Toast.makeText(context, "Нет подключения к интернету :(", Toast.LENGTH_LONG).show()
                                    }
                                }
                            )
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }

        orderViewModel.orderCreationRequest.observe(viewLifecycleOwner)
        {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "code: ${it.code} – ${it.errorMessage}")
                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")


                is ApiResponse.Success -> {
                    Log.i(TAG, "Successful create order ${it.data}")

                    cartViewModel.clearCart(object : CoroutinesErrorHandler {
                        override fun onError(message: String) {
                            TODO("Not yet implemented")
                        }
                    })

                    when (val clientResponse =
                        profileViewModel.clientAuthenticationResponse.value) {
                        is ApiResponse.Failure -> {
                            Log.e(
                                TAG,
                                "code: ${clientResponse.code} – ${clientResponse.errorMessage}"
                            )
                        }

                        is ApiResponse.Loading -> Log.i(TAG, "loading")

                        is ApiResponse.Success -> {
                            withDemoBottomSheet { dismiss() }
                            val directions =
                                CartFragmentDirections.actionCartFragmentToOrderReceiptFragment(
                                    clientResponse.data!!
                                )
                            findNavController().navigate(
                                directions
                            )
                        }

                        else -> {}
                    }

                }
            }
        }

        profileViewModel.tryLoadClient(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented")
            }
        })

        paymentMethodViewModel.tryLoadSelectedPaymentMethod(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented")
            }
        })

        pickUpLocationViewModel.tryLoadSelectedPickUpLocation(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented")
            }
        })
    }
}
