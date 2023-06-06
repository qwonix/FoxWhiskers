package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.repository.MenuCartRepository
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val menuCartRepository: MenuCartRepository
) : BaseViewModel() {
    private val TAG = "OrderViewModel"

    private val _cartTotalPrice = MutableLiveData<BigDecimal>(BigDecimal.ZERO)
    val cartTotalPrice: LiveData<BigDecimal> = _cartTotalPrice

    private val _cartTotalCount = MutableLiveData<Int>(0)
    val cartTotalCount: LiveData<Int> = _cartTotalCount

    // TODO: fix init load in CartFragment
    private val _cart = MutableLiveData<ApiResponse<List<Dish>>>(ApiResponse.Success(emptyList()))
    val cart: LiveData<ApiResponse<List<Dish>>> = _cart

    init {
        cart.observeForever {
            if (it is ApiResponse.Success) {
                _cartTotalPrice.postValue(it.data.sumOf { dish ->
                    BigDecimal(dish.currencyPrice).multiply((BigDecimal(dish.count)))
                })

                _cartTotalCount.postValue(it.data.sumOf { dish ->
                    dish.count
                })
            }
        }
    }

    fun changeDishCount(
        dish: Dish,
        newCount: Int,
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _cart,
        coroutinesErrorHandler
    ) {
        menuCartRepository.changeDishCount(dish, newCount)
    }

    fun getDishesInCart(): List<Dish> {
        val response = cart.value
        return (response as? ApiResponse.Success)?.data ?: emptyList()
    }


    fun load(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _cart,
        coroutinesErrorHandler
    ) {
        menuCartRepository.getDishesInCart()
    }

    fun clearCart(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _cart,
        coroutinesErrorHandler
    ) {
        menuCartRepository.clear()
    }
}
