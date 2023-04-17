package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.qwonix.android.foxwhiskers.BR
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.entity.DishType

class MenuViewModel : ViewModel() {

//    val errorMessage = MutableLiveData<String>()


    private val _dishes = MutableLiveData<List<Dish>>(emptyList())
    val dishes: LiveData<List<Dish>> = _dishes

    val orderPrice = MutableLiveData(0.0)

//    var job: Job? = null

    //    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
//        onError("Exception handled: ${throwable.localizedMessage}")
//    }
//    val loading = MutableLiveData<Boolean>()

    init {
        loadDishesMapByType()
    }


    fun loadDishesMapByType() {
        val data = getData()
        _dishes.postValue(data)

        data.map {
            it.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    if (propertyId == BR.count) {
                        val sumOf =
                            _dishes.value!!.sumOf { dish -> dish.count * dish.currencyPrice }
                        orderPrice.value = sumOf
                    }
                }
            })

            it.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    if (((sender as Dish).count == 0)) {
                        val dishes = _dishes.value!!.toMutableList()
                        _dishes.postValue(dishes)
                    }
                }
            })
        }


//        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
//            val response = mainRepository.getAllMovies()
//            withContext(Dispatchers.Main) {
//                if (response.isSuccessful) {
//                    dishTypeDishMap.postValue(response.body())
//                    loading.value = false
//                } else {
//                    onError("Error ${response.code()} : ${response.message()} ")
//                }
//            }
//        }
    }

    private fun getData(): List<Dish> {
        return listOf(
            Dish(
                1,
                "Пицца",
                "https://i.imgur.com/dNpAg7f.jpg",
                "целая, 42 см, 1350 гр",
                "2131.32 ₽",
                2131.32,
                DishType("Пицца")
            ),
            Dish(
                1,
                "Пицца",
                "https://i.imgur.com/H1ieAcE.png",
                "целая, 42 см, 1350 гр",
                "123 ₽",
                123.0,
                DishType("Пицца")
            ),
            Dish(
                1,
                "Пицца",
                "https://i.imgur.com/H1ieAcE.png",
                "целая, 42 см, 1350 гр",
                "669 ₽",
                669.0,
                DishType("Пицца")
            ),
            Dish(
                1,
                "Пицца",
                "https://i.imgur.com/kzUwGbe.jpg",
                "целая, 42 см, 1350 гр",
                "842 ₽",
                842.0,
                DishType("Пицца")
            )
        )
    }

//    private fun onError(message: String) {
//        errorMessage.value = message
//        loading.value = false
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        job?.cancel()
//    }

}
