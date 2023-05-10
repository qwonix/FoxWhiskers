package ru.qwonix.android.foxwhiskers.repository

import ru.qwonix.android.foxwhiskers.dto.AuthenticationRequestDTO
import ru.qwonix.android.foxwhiskers.service.AuthenticationService
import javax.inject.Inject


class AuthenticationRepository @Inject constructor(
    private val authenticationService: AuthenticationService
) {
    fun authenticate(phoneNumber: String, code: Int) = apiRequestFlow {
        val authenticationRequestDTO = AuthenticationRequestDTO(phoneNumber, code)
        return@apiRequestFlow authenticationService.authenticate(authenticationRequestDTO)
    }

    fun sendAuthenticationCode(phoneNumber: String) = apiRequestFlow {
        authenticationService.sendCode(phoneNumber)
    }
}