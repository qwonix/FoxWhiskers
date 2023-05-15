package ru.qwonix.android.foxwhiskers.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.qwonix.android.foxwhiskers.entity.Client
import ru.qwonix.android.foxwhiskers.entity.Order
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.repository.AuthenticationRepository
import ru.qwonix.android.foxwhiskers.repository.OrderRepository
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
    private val orderRepository: OrderRepository
) : BaseViewModel() {
    private val TAG = "OrderViewModel"

    private val _orders = MutableLiveData<ApiResponse<List<Order>>>()
    val orders: LiveData<ApiResponse<List<Order>>> = _orders

    private val _authenticatedClient = MutableLiveData<Client>()
    val authenticatedClient: LiveData<Client> = _authenticatedClient

    private val _clientAuthenticationResponse = MutableLiveData<ApiResponse<Client?>>()
    val clientAuthenticationResponse: LiveData<ApiResponse<Client?>> = _clientAuthenticationResponse

    init {
        clientAuthenticationResponse.observeForever {
            when (it) {
                is ApiResponse.Success -> {
                    Log.i(TAG, "${it} ${it.data}")
                    val data = it.data!!
                    _authenticatedClient.postValue(data)
                    loadOrders(object : CoroutinesErrorHandler {
                        override fun onError(message: String) {
                            TODO("Not yet implemented $message")
                        }
                    })
                }

                else -> {}
            }
        }
        tryLoadClient(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented")
            }
        })

    }

    fun tryLoadClient(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _clientAuthenticationResponse,
        coroutinesErrorHandler
    ) {
        authenticationRepository.loadClient()
    }


    private fun loadOrders(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _orders,
        coroutinesErrorHandler
    ) {
        Log.i(TAG, "client ${authenticatedClient.value}")
        orderRepository.all(authenticatedClient.value!!.phoneNumber)
    }


}
