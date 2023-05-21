package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.lifecycle.LiveData
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


    private val _selectedPaymentMethod = MutableLiveData<PaymentMethod>()
    val selectedPaymentMethod: LiveData<PaymentMethod> = _selectedPaymentMethod

    private val _selectedPaymentMethodResponse = MutableLiveData<ApiResponse<PaymentMethod?>>()

    init {
        _selectedPaymentMethodResponse.observeForever {
            if (it is ApiResponse.Success) {
                _selectedPaymentMethod.postValue(it.data!!)
            }
        }

        tryLoadSelectedPaymentMethod(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented")
            }
        })
    }

    fun setPaymentMethod(paymentMethod: PaymentMethod) {
        _selectedPaymentMethod.postValue(paymentMethod)
        paymentMethodRepository.setSelected(paymentMethod)
    }

    private fun tryLoadSelectedPaymentMethod(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _selectedPaymentMethodResponse, coroutinesErrorHandler
    ) {
        paymentMethodRepository.selected()
    }

}