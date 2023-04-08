package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.entity.DishType

class MenuViewModel : ViewModel() {

    val errorMessage = MutableLiveData<String>()

    private val _dishTypeDishMap = MutableLiveData<Map<DishType, List<Dish>>>(emptyMap())
    val dishTypeDishMap: LiveData<Map<DishType, List<Dish>>> = _dishTypeDishMap

    private val _cartDishes = MutableLiveData<List<Dish>>(emptyList())
    val cartDishes: LiveData<List<Dish>> = _cartDishes

    var job: Job? = null

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    fun loadDishesMapByType() {
        _dishTypeDishMap.postValue(getData())

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



    private fun getData(): Map<DishType, List<Dish>> {
        return mapOf(
            Pair(
                DishType("Пицца"),
                listOf(
                    Dish(
                        1,
                        "Пицца",
                        "https://i.imgur.com/dNpAg7f.jpg",
                        "целая, 42 см, 1350 гр",
                        "2131.32 ₽"
                    ),
                    Dish(
                        1,
                        "Пицца",
                        "https://i.imgur.com/H1ieAcE.png",
                        "целая, 42 см, 1350 гр",
                        "123 ₽"
                    ),
                    Dish(
                        1,
                        "Пицца",
                        "https://i.imgur.com/H1ieAcE.png",
                        "целая, 42 см, 1350 гр",
                        "669 ₽"
                    ),
                    Dish(
                        1,
                        "Пицца",
                        "https://i.imgur.com/kzUwGbe.jpg",
                        "целая, 42 см, 1350 гр",
                        "842 ₽"
                    )
                )
            ),
            Pair(
                DishType("Суп"),
                listOf(
                    Dish(
                        1,
                        "Пицца",
                        "https://i.imgur.com/dNpAg7f.jpg",
                        "целая, 42 см, 1350 гр",
                        "2131.32 ₽"
                    ),
                    Dish(
                        1,
                        "Пицца",
                        "https://i.imgur.com/H1ieAcE.png",
                        "целая, 42 см, 1350 гр",
                        "123 ₽"
                    ),
                    Dish(
                        1,
                        "Пицца",
                        "https://i.imgur.com/H1ieAcE.png",
                        "целая, 42 см, 1350 гр",
                        "669 ₽"
                    ),
                    Dish(
                        1,
                        "Пицца",
                        "https://i.imgur.com/kzUwGbe.jpg",
                        "целая, 42 см, 1350 гр",
                        "842 ₽"
                    )
                )
            )
        )
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
