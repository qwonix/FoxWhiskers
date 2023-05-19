package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.qwonix.android.foxwhiskers.entity.PaymentMethod
import ru.qwonix.android.foxwhiskers.entity.PickUpLocationSettings
import ru.qwonix.android.foxwhiskers.entity.Settings
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.repository.SettingsRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : BaseViewModel() {

    private val _pickUpLocationSettings = MutableLiveData<PickUpLocationSettings>()
    val pickUpLocationSettings: LiveData<PickUpLocationSettings> = _pickUpLocationSettings

    private val _paymentMethod = MutableLiveData<PaymentMethod>(PaymentMethod.CASH)
    val paymentMethod: LiveData<PaymentMethod> = _paymentMethod

    private val _settingsResponse = MutableLiveData<ApiResponse<Settings>>()
    val settingsResponse: LiveData<ApiResponse<Settings>> = _settingsResponse

    init {
        tryLoadSettings(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented")
            }
        })

        settingsResponse.observeForever {
            if (it is ApiResponse.Success) {
                _pickUpLocationSettings.postValue(it.data.pickUpLocationSettings!!)
                _paymentMethod.postValue(it.data.paymentMethod!!)
            }
        }
    }


    fun tryLoadSettings(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _settingsResponse, coroutinesErrorHandler
    ) {
        settingsRepository.load()
    }
}