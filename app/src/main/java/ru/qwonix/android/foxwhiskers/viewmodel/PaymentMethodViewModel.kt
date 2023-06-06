package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.qwonix.android.foxwhiskers.entity.PaymentMethod
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.repository.PaymentMethodRepository
import javax.inject.Inject

@HiltViewModel
class PaymentMethodViewModel @Inject constructor(
    private val paymentMethodRepository: PaymentMethodRepository
) : BaseViewModel() {

    val selectedPaymentMethodResponse = MutableLiveData<ApiResponse<PaymentMethod?>>()

    fun setPaymentMethod(
        paymentMethod: PaymentMethod,
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) {
        baseRequest(selectedPaymentMethodResponse, coroutinesErrorHandler) {
            paymentMethodRepository.setSelected(paymentMethod)
        }
    }


    fun tryLoadSelectedPaymentMethod(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        selectedPaymentMethodResponse,
        coroutinesErrorHandler
    ) {
        paymentMethodRepository.selected()
    }

}