package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.qwonix.android.foxwhiskers.dto.OrderCreationItemDTO
import ru.qwonix.android.foxwhiskers.dto.OrderCreationRequestDTO
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.entity.Order
import ru.qwonix.android.foxwhiskers.entity.PaymentMethod
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.repository.OrderRepository
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : BaseViewModel() {
    private val TAG = "OrderViewModel"

    private val _orders = MutableLiveData<ApiResponse<List<Order>>>()
    val orders: LiveData<ApiResponse<List<Order>>> = _orders

    private val _orderCreationRequest = MutableLiveData<ApiResponse<Order>>()
    val orderCreationRequest = _orderCreationRequest

    fun loadOrders(
        phone_number: String,
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _orders,
        coroutinesErrorHandler
    ) {
        orderRepository.all(phone_number)
    }

    fun createOrder(
        phone_number: String,
        cart: List<Dish>,
        pickUpLocationId: Long,
        paymentMethod: PaymentMethod,
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _orderCreationRequest,
        coroutinesErrorHandler
    ) {
        val orderCreationRequestDTO = OrderCreationRequestDTO(
            phone_number,
            cart.map { OrderCreationItemDTO(it.id, it.count) },
            pickUpLocationId,
            paymentMethod
        )

        orderRepository.create(orderCreationRequestDTO)
    }

}
