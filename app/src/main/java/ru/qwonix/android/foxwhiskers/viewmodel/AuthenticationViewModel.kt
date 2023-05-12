package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.qwonix.android.foxwhiskers.dto.AuthenticationResponseDTO
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.repository.AuthenticationRepository
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authenticationRepository: AuthenticationRepository,
) : BaseViewModel() {

    private val _authenticationResponse = MutableLiveData<ApiResponse<AuthenticationResponseDTO>>()
    val authenticationResponse = _authenticationResponse

    private val _sendCodeResponse = MutableLiveData<ApiResponse<Boolean>>()
    val sendCodeResponse = _sendCodeResponse

    fun authenticate(
        phoneNumber: String,
        code: Int,
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(_authenticationResponse, coroutinesErrorHandler) {
        authenticationRepository.authenticate(phoneNumber, code)
    }

    fun sendCode(
        phoneNumber: String,
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _sendCodeResponse,
        coroutinesErrorHandler
    ) {
        authenticationRepository.sendAuthenticationCode(phoneNumber)
    }

    fun sendCodeAgain(
        phoneNumber: String,
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(coroutinesErrorHandler) {
        authenticationRepository.sendAuthenticationCode(phoneNumber)
    }
}