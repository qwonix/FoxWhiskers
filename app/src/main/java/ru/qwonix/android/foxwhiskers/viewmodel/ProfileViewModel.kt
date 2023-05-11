package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.qwonix.android.foxwhiskers.dto.UpdateUserProfileDTO
import ru.qwonix.android.foxwhiskers.entity.UserProfile
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.repository.AuthenticationRepository
import ru.qwonix.android.foxwhiskers.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository
) : BaseViewModel() {

    private val _authenticatedUser = MutableLiveData<ApiResponse<UserProfile?>>()
    val authenticatedUser = _authenticatedUser

    private val _updatedUser = MutableLiveData<ApiResponse<UserProfile?>>()
    val updatedUser = _updatedUser

    fun tryLoadUserProfile(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _authenticatedUser,
        coroutinesErrorHandler
    ) {
        authenticationRepository.loadUserProfile()
    }

    fun update(
        phoneNumber: String,
        firstName: String,
        lastName: String,
        email: String,
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _updatedUser,
        coroutinesErrorHandler
    ) {
        val updateUserProfileDTO = UpdateUserProfileDTO(phoneNumber, firstName, lastName, email)
        userRepository.update(updateUserProfileDTO)
    }


    fun logout(coroutinesErrorHandler: CoroutinesErrorHandler) = baseRequest(
        MutableLiveData(),
        coroutinesErrorHandler
    ) {
        userRepository.logout()
    }

    fun isRequiredForEdit(data: UserProfile): Boolean {
        return data.firstName.isNullOrBlank() || data.lastName.isNullOrBlank() || data.email.isNullOrBlank()
    }
}