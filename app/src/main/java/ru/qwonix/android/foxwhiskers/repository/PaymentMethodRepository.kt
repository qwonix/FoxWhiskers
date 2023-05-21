package ru.qwonix.android.foxwhiskers.repository

import ru.qwonix.android.foxwhiskers.entity.PaymentMethod
import ru.qwonix.android.foxwhiskers.service.LocalSettingsService
import ru.qwonix.android.foxwhiskers.service.PaymentMethodService
import javax.inject.Inject

class PaymentMethodRepository @Inject constructor(
    private val paymentMethodService: PaymentMethodService,
    private val settingsService: LocalSettingsService
) {

    fun selected() = requestFlow {
        var selectedPaymentMethod = settingsService.loadSelectedPaymentMethod()
        if (selectedPaymentMethod == null) {
            val response = paymentMethodService.findMaxPriority()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                selectedPaymentMethod = body
            }
        }
        selectedPaymentMethod
    }

    fun setSelected(paymentMethod: PaymentMethod) = requestFlow {
        settingsService.saveSelectedPaymentMethod(paymentMethod)
    }

}