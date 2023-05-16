package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.qwonix.android.foxwhiskers.entity.Client
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.repository.AuthenticationRepository
import ru.qwonix.android.foxwhiskers.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository
) : BaseViewModel() {

    private val TAG = "ProfileViewModel"

    private val _clientAuthenticationResponse = MutableLiveData<ApiResponse<Client?>>()
    val clientAuthenticationResponse: LiveData<ApiResponse<Client?>> = _clientAuthenticationResponse


    fun tryLoadClient(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _clientAuthenticationResponse,
        coroutinesErrorHandler
    ) {
        authenticationRepository.loadClient()
    }


    fun logout(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        MutableLiveData(),
        coroutinesErrorHandler
    ) {
        userRepository.logout()
    }


    fun isRequiredForEdit(data: Client): Boolean {
        return data.firstName.isNullOrBlank() || data.lastName.isNullOrBlank() || data.email.isNullOrBlank()
    }
}