package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.qwonix.android.foxwhiskers.InvalidPhoneNumberException
import ru.qwonix.android.foxwhiskers.repository.InMemoryRepository

class LoginViewModel : ViewModel() {

    private val _authenticationIsSuccessful: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val authenticationIsSuccessful: LiveData<Boolean> = _authenticationIsSuccessful

    var phoneNumber: String? = null
        set(value) {
            if (value.isNullOrBlank()) {
                throw InvalidPhoneNumberException("empty phone number")
            }
            field = value
        }

    private var foxWhiskersRepository = InMemoryRepository.getInstance()

    fun checkCode(code: String) {
//        foxWhiskersRepository.authenticate(phoneNumber, code)
    }

    fun sendCode() {
//        foxWhiskersRepository.sendAuthenticationSmsCodeToNumber(phoneNumber)
    }
}