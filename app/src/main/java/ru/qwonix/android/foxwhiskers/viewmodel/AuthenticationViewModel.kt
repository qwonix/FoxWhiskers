package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ru.qwonix.android.foxwhiskers.dto.AuthenticationResponseDTO
import ru.qwonix.android.foxwhiskers.dto.UpdateUserProfileDTO
import ru.qwonix.android.foxwhiskers.entity.UserProfile
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.repository.AuthenticationRepository
import ru.qwonix.android.foxwhiskers.repository.LocalTokenStorageRepository
import ru.qwonix.android.foxwhiskers.repository.LocalUserStorageRepository
import ru.qwonix.android.foxwhiskers.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val localUserStorageRepository: LocalUserStorageRepository,
    private val localTokenStorageRepository: LocalTokenStorageRepository,
) : BaseViewModel() {

    private val _authenticatedUser = MutableLiveData<ApiResponse<UserProfile?>>()
    val authenticatedUser = _authenticatedUser

    private val _authenticatedResponse = MutableLiveData<ApiResponse<AuthenticationResponseDTO>>()
    val authenticatedResponse = _authenticatedResponse

    init {
        authenticate(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented")
            }
        })
    }

    fun authenticate(
        accessToken: String,
        refreshToken: String,
        userProfile: UserProfile
    ) {
        _authenticatedUser.postValue(ApiResponse.Success(userProfile))

        viewModelScope.launch {
            localUserStorageRepository.saveUserProfile(userProfile).collect()
            localTokenStorageRepository.saveAccessToken(accessToken).collect()
            localTokenStorageRepository.saveRefreshToken(refreshToken).collect()
        }
    }

    fun authenticate(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _authenticatedUser,
        coroutinesErrorHandler
    ) {
        localUserStorageRepository.loadUserProfile()
    }


    fun authenticate(
        phoneNumber: String, code: Int,
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) {
        baseRequest(_authenticatedResponse, coroutinesErrorHandler) {
            authenticationRepository.authenticate(phoneNumber, code)
        }
    }

    fun logout() {
        localTokenStorageRepository.deleteAccessToken()
        localTokenStorageRepository.deleteRefreshToken()
    }

    fun login(data: UserProfile, accessToken: String, refreshToken: String) {
        localUserStorageRepository.saveUserProfile(data)
        localTokenStorageRepository.saveAccessToken(accessToken)
        localTokenStorageRepository.saveRefreshToken(refreshToken)
    }

    fun sendCode(phoneNumber: String, coroutinesErrorHandler: CoroutinesErrorHandler) = baseRequest(
        // TODO: loading
        MutableLiveData(),
        coroutinesErrorHandler
    ) {
        authenticationRepository.sendAuthenticationCode(phoneNumber)
    }

    fun update(
        firstName: String,
        lastName: String,
        email: String,
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        // TODO: loading
        MutableLiveData(),
        coroutinesErrorHandler
    ) {
        val updateUserProfileDTO = UpdateUserProfileDTO(firstName, lastName, email)
        userRepository.update(updateUserProfileDTO)
    }

    fun isRequiredForEdit(data: UserProfile): Boolean {
        return data.firstName.isNullOrBlank() || data.lastName.isNullOrBlank() || data.email.isNullOrBlank()
    }
}