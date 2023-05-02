package ru.qwonix.android.foxwhiskers.viewmodel

import android.util.Log
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
import ru.qwonix.android.foxwhiskers.InvalidPhoneNumberException
import ru.qwonix.android.foxwhiskers.entity.UserProfile
import ru.qwonix.android.foxwhiskers.service.AuthenticationService
import ru.qwonix.android.foxwhiskers.util.Utils
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val authenticationService: AuthenticationService,
) : ViewModel() {

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    private val _userProfile: MutableLiveData<UserProfile?> = MutableLiveData()

    val userProfile: LiveData<UserProfile?> = _userProfile

    private val _phoneNumber: MutableLiveData<String?> = MutableLiveData()
//    val phoneNumber: LiveData<String?> = _phoneNumber

    init {
        userProfile.observeForever {
            job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                withContext(Dispatchers.Main) {
                    if (it != null) {
                        authenticationService.saveUserProfile(it)
                    } else {
                        authenticationService.clearUserProfile()
                    }
                }
            }
        }

        loadProfile()
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

    var phoneNumber: String = ""
        set(value) {
            if (value.isNullOrBlank()) {
                throw InvalidPhoneNumberException("empty phone number")
            }
            field = value
        }


    fun loadProfile() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = authenticationService.loadUserProfile()
            withContext(Dispatchers.Main) {
                if (response == null) {
                    _userProfile.postValue(null)
                    onError("Error ${response} : loadProfile ")
                } else {
                    _userProfile.postValue(response)
                }
            }
        }
    }

    fun authenticateWithPinCode(code: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val authenticationResponse = authenticationService.authenticate(phoneNumber, code)
            withContext(Dispatchers.Main) {
                if (authenticationResponse.isSuccessful && authenticationResponse.data != null) {
                    val authenticationResponseDTO = authenticationResponse.data
                    val loadedUserProfile = authenticationService.loadUserProfile(
                        phoneNumber,
                        authenticationResponseDTO.jwtAccessToken
                    )
                    if (loadedUserProfile.isSuccessful) {
                        _userProfile.postValue(loadedUserProfile.data)
                    } else {
                        onError("Error ${loadedUserProfile.code} : ${loadedUserProfile.message} ")
                    }
                } else {
                    onError("Error ${authenticationResponse.code} : ${authenticationResponse.message} ")
                }
            }
        }
    }

    fun updateProfile(userProfile: UserProfile) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = authenticationService.updateProfile(userProfile)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _userProfile.postValue(response.data)
                }
            }
        }
    }

    fun sendCode() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = authenticationService.sendAuthenticationSmsCodeToNumber(phoneNumber)
        }
    }

    private fun onError(message: String) {
        Log.e("tag", message)
    }
}