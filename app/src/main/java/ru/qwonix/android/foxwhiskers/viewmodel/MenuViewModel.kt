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
import ru.qwonix.android.foxwhiskers.BR
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.entity.PickUpLocation
import ru.qwonix.android.foxwhiskers.repository.InMemoryRepository
import java.math.BigDecimal
import java.math.BigInteger

class MenuViewModel : ViewModel() {

    private var foxWhiskersRepository = InMemoryRepository.getInstance()

    var job: Job? = null
    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()

    val selectedPickUpLocation: MutableLiveData<PickUpLocation> = MutableLiveData()

    private val _locations: MutableLiveData<List<PickUpLocation>> = MutableLiveData()
    val locations: LiveData<List<PickUpLocation>> = _locations

    private val _dishes: MutableLiveData<List<Dish>> = MutableLiveData()
    val dishes: LiveData<List<Dish>> = _dishes

    val orderPrice = MutableLiveData(BigDecimal(BigInteger.ZERO))

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    init {
        _locations.observeForever { selectedPickUpLocation.postValue(it.maxBy { location -> location.priority }) }

        loadDishes()
        loadLocations()
    }

    fun setSelectedLocation(pickUpLocation: PickUpLocation) {
        this.selectedPickUpLocation.postValue(pickUpLocation)
    }

    fun loadDishes() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = foxWhiskersRepository.findAllDishes()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val data = response.data
                    _dishes.postValue(data)


                    data.map {
                        it.addOnPropertyChangedCallback(object :
                            Observable.OnPropertyChangedCallback() {
                            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                if (propertyId == BR.count) {
                                    val sumOf =
                                        _dishes.value!!.sumOf { dish -> dish.count * dish.currencyPrice }
                                    orderPrice.value = BigDecimal(sumOf)
                                }
                            }
                        })

                        it.addOnPropertyChangedCallback(object :
                            Observable.OnPropertyChangedCallback() {
                            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                                if (((sender as Dish).count == 0)) {
                                    val dishes = _dishes.value!!.toMutableList()
                                    _dishes.postValue(dishes)
                                }
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
