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
import ru.qwonix.android.foxwhiskers.entity.UserProfile
import ru.qwonix.android.foxwhiskers.service.AuthenticationService
import ru.qwonix.android.foxwhiskers.service.LocalStorageService
import ru.qwonix.android.foxwhiskers.util.Utils
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val localStorageService: LocalStorageService,
    private val authenticationService: AuthenticationService,
) : ViewModel() {

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    private val _loggedUserProfile: MutableLiveData<UserProfile?> = MutableLiveData()
    val loggedUserProfile: LiveData<UserProfile?> = _loggedUserProfile

    init {
        tryLoadProfile()
    }

    fun tryLoadProfile() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = localStorageService.loadUserProfile()
            withContext(Dispatchers.Main) {
                _loggedUserProfile.postValue(response.data)
            }
        }
    }

    fun authenticateWithPinCode(phoneNumber: String, code: Int) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val authenticationResponse = authenticationService.authenticate(phoneNumber, code)
            withContext(Dispatchers.Main) {
                if (authenticationResponse.isSuccessful && authenticationResponse.data != null) {
                    val authenticationResponseDTO = authenticationResponse.data
                    val loadedUserProfile = authenticationService.findUserProfile(
                        phoneNumber,
                        authenticationResponseDTO.jwtAccessToken
                    )

                    if (loadedUserProfile.isSuccessful && loadedUserProfile.data != null) {
                        _loggedUserProfile.postValue(loadedUserProfile.data)

                        localStorageService.clearUserProfile()
                        localStorageService.saveUserProfile(loadedUserProfile.data)
                    } else {
                        onError("Error ${loadedUserProfile.code} : ${loadedUserProfile.message} ")
                    }
                } else {
                    onError("Error ${authenticationResponse.code} : ${authenticationResponse.message} ")
                }
            }
        }
    }

    suspend fun updateProfile(userProfileToUpdate: UserProfile) : UserProfile? {
        val response = authenticationService.updateProfile(userProfileToUpdate)

        val updatedUserProfile = response.data
        _loggedUserProfile.postValue(updatedUserProfile)

        if (response.isSuccessful && updatedUserProfile != null) {
            localStorageService.clearUserProfile()
            localStorageService.saveUserProfile(updatedUserProfile)
        }

        return updatedUserProfile
    }

    fun sendCode(phoneNumber: String) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            authenticationService.sendAuthenticationSmsCodeToNumber(phoneNumber)
        }
    }

    suspend fun logout() {
        localStorageService.clearUserProfile()
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


    private fun onError(message: String) {
        Log.e("tag", message)
    }
}