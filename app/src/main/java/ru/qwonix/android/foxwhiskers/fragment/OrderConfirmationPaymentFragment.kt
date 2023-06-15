package ru.qwonix.android.foxwhiskers.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.qwonix.android.foxwhiskers.R
import ru.qwonix.android.foxwhiskers.databinding.FragmentOrderConfirmationPaymentBinding
import ru.qwonix.android.foxwhiskers.entity.PaymentMethod
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.util.withDemoBottomSheet
import ru.qwonix.android.foxwhiskers.viewmodel.CoroutinesErrorHandler
import ru.qwonix.android.foxwhiskers.viewmodel.PaymentMethodViewModel
import ru.qwonix.android.foxwhiskers.viewmodel.ProfileViewModel


@AndroidEntryPoint
class OrderConfirmationPaymentFragment : Fragment(R.layout.fragment_order_confirmation_payment) {

    private val TAG = "OrderConfirmPaymentFrag"

    interface PaymentMethodChangeListener {
        fun onSelectedPaymentMethodChange(paymentMethod: PaymentMethod)
    }

    companion object {
        fun newInstance() = OrderConfirmationPaymentFragment()

        @JvmStatic
        @BindingAdapter("android:src")
        fun setImageViewResource(imageView: ImageView, resource: Int) {
            imageView.setImageResource(resource)
        }
    }

    private val paymentMethodViewModel: PaymentMethodViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private lateinit var binding: FragmentOrderConfirmationPaymentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderConfirmationPaymentBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            email = "Не указана"
            paymentMethodChangeListener = object : PaymentMethodChangeListener {
                override fun onSelectedPaymentMethodChange(paymentMethod: PaymentMethod) {
                    paymentMethodViewModel.setPaymentMethod(paymentMethod,
                        object : CoroutinesErrorHandler {
                            override fun onError(message: String) {
                                TODO("Not yet implemented")
                            }
                        })
                }
            }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentMethodViewModel.selectedPaymentMethodResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "fail to load profile code: ${it.code} – ${it.errorMessage}")
                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")

                is ApiResponse.Success -> {
                    binding.selectedPaymentMethod = it.data
                }
            }
        }

        profileViewModel.clientAuthenticationResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "fail to load profile code: ${it.code} – ${it.errorMessage}")
                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")

                is ApiResponse.Success -> {
                    binding.email = it.data?.email ?: "Не указана"
                }
            }
        }

        binding.goBackButton.setOnClickListener {
            withDemoBottomSheet { goBack() }
        }

        paymentMethodViewModel.tryLoadSelectedPaymentMethod(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented")
            }
        })
        profileViewModel.tryLoadClientFromLocalStorage(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented")
            }
        })
    }
}
