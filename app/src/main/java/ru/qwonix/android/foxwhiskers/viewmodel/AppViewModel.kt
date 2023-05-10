package ru.qwonix.android.foxwhiskers.viewmodel

import android.util.Log
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.entity.PaymentMethod
import ru.qwonix.android.foxwhiskers.entity.PickUpLocation
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.repository.MenuRepository
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val menuRepository: MenuRepository
) : BaseViewModel() {

    var job: Job? = null
    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    val selectedPaymentMethod: MutableLiveData<PaymentMethod> =
        MutableLiveData(PaymentMethod.INAPP_ONLINE_CARD)

    val selectedPickUpLocation: MutableLiveData<PickUpLocation> = MutableLiveData()

    private val _locations: MutableLiveData<List<PickUpLocation>> = MutableLiveData()
    val locations: LiveData<List<PickUpLocation>> = _locations

    private val _dishes: MutableLiveData<ApiResponse<List<Dish>>> = MutableLiveData()
    val dishes: LiveData<ApiResponse<List<Dish>>> = _dishes

    private val _orderCart: MutableLiveData<List<Dish>> = MutableLiveData(emptyList())
    val orderCart: LiveData<List<Dish>> = _orderCart

    init {
        _locations.observeForever { selectedPickUpLocation.postValue(it.maxBy { location -> location.priority }) }

        dishes.observeForever {
            when (it) {
                is ApiResponse.Failure -> {
                    "Code: ${it.code}, ${it.errorMessage}"
                }

                ApiResponse.Loading -> "Loading"

                is ApiResponse.Success -> {
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
                Log.e("confirmEditingButton", "Error! $message")
            }
        })
        loadLocations(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                Log.e("confirmEditingButton", "Error! $message")
            }
        })
    }

    fun setSelectedLocation(pickUpLocation: PickUpLocation) {
        this.selectedPickUpLocation.postValue(pickUpLocation)
    }

    fun setSelectedPaymentMethod(paymentMethod: PaymentMethod) {
        this.selectedPaymentMethod.postValue(paymentMethod)
    }

    fun loadDishes(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) {
        baseRequest(
            _dishes,
            coroutinesErrorHandler
        ) {
            menuRepository.findAllDishes()
        }
    }


    fun loadLocations(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) {
        val first: ApiResponse<List<PickUpLocation>>
        runBlocking {
            first = menuRepository.findAllLocations().first()
        }
        when (first) {
            is ApiResponse.Failure -> Log.e("", "")
            is ApiResponse.Success -> {
                _locations.postValue(first.data)
            }

            is ApiResponse.Loading -> Log.e("", "")
        }
    }


    private fun onError(message: String) {
        Log.e("tag", message)
        errorMessage.postValue(message)
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
