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
import ru.qwonix.android.foxwhiskers.retrofit.AuthenticationRepository
import ru.qwonix.android.foxwhiskers.retrofit.LocalStorageRepository
import ru.qwonix.android.foxwhiskers.util.Utils
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val localStorageRepository: LocalStorageRepository,
    private val authenticationRepository: AuthenticationRepository,
) : ViewModel() {

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    private val _loggedUserProfile: MutableLiveData<UserProfile?> = MutableLiveData()
    val loggedUserProfile: LiveData<UserProfile?> = _loggedUserProfile

    init {
//        tryLoadProfile()
    }

    fun tryLoadProfile() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = localStorageRepository.loadUserProfile()
            withContext(Dispatchers.Main) {
                _loggedUserProfile.postValue(response)
            }
        }
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

    suspend fun updateProfile(userProfileToUpdate: UserProfile) : UserProfile? {
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


    private fun onError(message: String) {
        Log.e("tag", message)
    }
}