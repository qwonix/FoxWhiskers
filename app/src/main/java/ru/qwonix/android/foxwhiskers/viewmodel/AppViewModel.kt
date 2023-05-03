package ru.qwonix.android.foxwhiskers.viewmodel

import android.util.Log
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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
import ru.qwonix.android.foxwhiskers.retrofit.AuthenticationRepository
import ru.qwonix.android.foxwhiskers.retrofit.LocalStorageRepository
import ru.qwonix.android.foxwhiskers.retrofit.MenuRepository
import ru.qwonix.android.foxwhiskers.util.Utils
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val localStorageRepository: LocalStorageRepository,
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {

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

    private val _dishes: MutableLiveData<List<Dish>> = MutableLiveData()
    val dishes: LiveData<List<Dish>> = _dishes

    private val _orderCart: MutableLiveData<List<Dish>> = MutableLiveData(emptyList())
    val orderCart: LiveData<List<Dish>> = _orderCart

    private val _loggedUserProfile: MutableLiveData<UserProfile?> = MutableLiveData()
    val loggedUserProfile: LiveData<UserProfile?> = _loggedUserProfile

    init {
        _locations.observeForever { selectedPickUpLocation.postValue(it.maxBy { location -> location.priority }) }

        loadDishes()
        loadLocations()
    }


    fun authenticateWithPinCode(phoneNumber: String, code: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val authenticationResponse = authenticationRepository.authenticate(phoneNumber, code)
            withContext(Dispatchers.Main) {
                val authenticationResponseDTO = authenticationResponse.body()
                if (authenticationResponse.isSuccessful && authenticationResponseDTO != null) {
                    val loadedUserProfile = authenticationRepository.findUserProfile(
                        phoneNumber,
                        authenticationResponseDTO.jwtAccessToken
                    )

                    val userProfile = loadedUserProfile.body()
                    if (loadedUserProfile.isSuccessful && userProfile != null) {
                        _loggedUserProfile.postValue(userProfile)

                        localStorageRepository.clearUserProfile()
                        localStorageRepository.saveUserProfile(userProfile)
                    } else {
                        onError("Error $userProfile : ${loadedUserProfile.code()} ")
                    }
                } else {
                    onError("Error ${authenticationResponse.code()} : ${authenticationResponse.errorBody()} ")
                }
            }
        }
    }

    suspend fun updateProfile(userProfileToUpdate: UserProfile): UserProfile? {
        val response = authenticationRepository.updateProfile(userProfileToUpdate)

        val updatedUserProfile = response.body()
        _loggedUserProfile.postValue(updatedUserProfile)

        if (response.isSuccessful && updatedUserProfile != null) {
            localStorageRepository.clearUserProfile()
            localStorageRepository.saveUserProfile(updatedUserProfile)
        }

        return updatedUserProfile
    }

    fun sendCode(phoneNumber: String) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            authenticationRepository.sendAuthenticationSmsCodeToNumber(phoneNumber)
        }
    }

    suspend fun logout() {
        localStorageRepository.clearUserProfile()
        _loggedUserProfile.postValue(null)
    }

    fun isValidFirstName(firstName: String): Boolean {
        return Utils.FIRSTNAME_REGEX.matches(firstName)
    }

    fun isValidLastName(lastName: String): Boolean {
        return Utils.LASTNAME_REGEX.matches(lastName)
    }

    fun isValidEmail(email: String): Boolean {
        return Utils.EMAIL_REGEX.matches(email)
    }

    fun tryLoadProfile() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = localStorageRepository.loadUserProfile()
            withContext(Dispatchers.Main) {
                _loggedUserProfile.postValue(response)
            }
        }
    }

    fun setSelectedLocation(pickUpLocation: PickUpLocation) {
        this.selectedPickUpLocation.postValue(pickUpLocation)
    }

    fun setSelectedPaymentMethod(paymentMethod: PaymentMethod) {
        this.selectedPaymentMethod.postValue(paymentMethod)
    }

    fun loadDishes() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = menuRepository.findAllDishes()
            withContext(Dispatchers.Main) {
                val data = response.body()
                if (response.isSuccessful && data != null) {
                    _dishes.postValue(data!!)
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
                    onError("Error ${response.code()} : ${response.errorBody()} ")
                }
            }
        }
    }

    fun loadLocations() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = menuRepository.findAllLocations()
            withContext(Dispatchers.Main) {
                val data = response.body()
                if (response.isSuccessful && data != null) {
                    _locations.postValue(data!!)
                    loading.value = false
                } else {
                    onError("Error ${response.errorBody()} : ${response.code()} ")
                }
            }
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
