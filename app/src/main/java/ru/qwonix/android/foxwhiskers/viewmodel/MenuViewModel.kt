package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.entity.PaymentMethod
import ru.qwonix.android.foxwhiskers.entity.PickUpLocation
import ru.qwonix.android.foxwhiskers.entity.UserProfile
import ru.qwonix.android.foxwhiskers.repository.InMemoryRepository
import java.math.BigDecimal
import java.math.BigInteger

class MenuViewModel : ViewModel() {

    private var foxWhiskersRepository = InMemoryRepository.getInstance()

    var job: Job? = null
    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()

    var userProfile: UserProfile? = null
    fun isUserLogged(): Boolean = userProfile != null

    val selectedPaymentMethod: MutableLiveData<PaymentMethod> =
        MutableLiveData(PaymentMethod.INAPP_ONLINE_CARD)

    val selectedPickUpLocation: MutableLiveData<PickUpLocation> = MutableLiveData()

    private val _locations: MutableLiveData<List<PickUpLocation>> = MutableLiveData()
    val locations: LiveData<List<PickUpLocation>> = _locations

    private val _dishes: MutableLiveData<List<Dish>> = MutableLiveData()
    val dishes: LiveData<List<Dish>> = _dishes

    private val _orderCart: MutableLiveData<List<Dish>> = MutableLiveData(emptyList())
    val orderCart: LiveData<List<Dish>> = _orderCart

    val orderPrice = MutableLiveData(BigDecimal(BigInteger.ZERO))

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    init {
        _locations.observeForever { selectedPickUpLocation.postValue(it.maxBy { location -> location.priority }) }

        _orderCart.observeForever { it ->
            orderPrice.postValue(it.sumOf {
                BigDecimal(it.currencyPrice).multiply((BigDecimal(it.count)))
            })
        }

        loadDishes()
        loadLocations()
    }

    fun setSelectedLocation(pickUpLocation: PickUpLocation) {
        this.selectedPickUpLocation.postValue(pickUpLocation)
    }

    fun setSelectedPaymentMethod(paymentMethod: PaymentMethod) {
        this.selectedPaymentMethod.postValue(paymentMethod)
    }

    fun loadProfile() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = foxWhiskersRepository.findUserProfile()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val data = response.data
                    userProfile = data
                } else {
                    onError("Error ${response.code} : ${response.message} ")
                }
            }
        }
    }

    fun loadDishes() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = foxWhiskersRepository.findAllDishes()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val data = response.data
                    _dishes.postValue(data)

                    data.map { data ->
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
                    loading.value = false
                } else {
                    onError("Error ${response.code} : ${response.message} ")
                }
            }
        }
    }

    fun loadLocations() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = foxWhiskersRepository.findAllLocations()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val data = response.data
                    _locations.postValue(data)

                    loading.value = false
                } else {
                    onError("Error ${response.code} : ${response.message} ")
                }
            }
        }
    }

    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }


}
