package ru.qwonix.android.foxwhiskers.viewmodel

import android.util.Log
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.entity.PaymentMethod
import ru.qwonix.android.foxwhiskers.entity.PickUpLocation
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.repository.MenuRepository
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRepository: MenuRepository
) : BaseViewModel() {
    private val TAG = "MenuViewModel"

    val selectedPaymentMethod: MutableLiveData<PaymentMethod> =
        MutableLiveData(PaymentMethod.INAPP_ONLINE_CARD)

    val selectedPickUpLocation: MutableLiveData<PickUpLocation> = MutableLiveData()

    private val _locations: MutableLiveData<ApiResponse<List<PickUpLocation>>> = MutableLiveData()
    val locations: LiveData<ApiResponse<List<PickUpLocation>>> = _locations

    private val _dishes: MutableLiveData<ApiResponse<List<Dish>>> = MutableLiveData()
    val dishes: LiveData<ApiResponse<List<Dish>>> = _dishes

    private val _orderCart: MutableLiveData<List<Dish>> = MutableLiveData(emptyList())
    val orderCart: LiveData<List<Dish>> = _orderCart

    init {
        locations.observeForever {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "code: ${it.code} – ${it.errorMessage}")
                }

                is ApiResponse.Loading -> Log.i(TAG, "loading")

                is ApiResponse.Success -> {
                    Log.i(TAG, "Successful load locations ${it.data}")
                    selectedPickUpLocation.postValue(it.data.maxBy { it.priority })
                }
            }
        }

        dishes.observeForever {
            when (it) {
                is ApiResponse.Failure -> {
                    Log.e(TAG, "code: ${it.code} – ${it.errorMessage}")
                }

                ApiResponse.Loading -> Log.i(TAG, "loading")


                is ApiResponse.Success -> {
                    Log.i(TAG, "Successful load dishes - ${it.data}")

                    it.data.map { data ->
                        data.addOnPropertyChangedCallback(object :
                            Observable.OnPropertyChangedCallback() {
                            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                var newCart: List<Dish> = _orderCart.value!!
                                val dish = sender as Dish

                                if (dish.count == 1 && dish !in newCart) {
                                    newCart = newCart.plus(sender)
                                } else if ((sender.count == 0)) {
                                    newCart = newCart.minus(sender)
                                }

                                _orderCart.postValue(newCart)
                            }
                        })
                    }
                }
            }
        }


        loadDishes(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented")
            }
        })

        loadLocations(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented")
            }
        })
    }

    fun setSelectedLocation(pickUpLocation: PickUpLocation) {
        this.selectedPickUpLocation.postValue(pickUpLocation)
    }

    fun setSelectedPaymentMethod(paymentMethod: PaymentMethod) {
        this.selectedPaymentMethod.postValue(paymentMethod)
    }

    private fun loadDishes(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _dishes,
        coroutinesErrorHandler
    ) {
        menuRepository.findAllDishes()
    }


    fun loadLocations(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _locations,
        coroutinesErrorHandler
    ) {
        menuRepository.findAllLocations()
    }
}
