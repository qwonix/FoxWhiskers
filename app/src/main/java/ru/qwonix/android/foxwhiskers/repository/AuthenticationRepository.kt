package ru.qwonix.android.foxwhiskers.repository

import okhttp3.internal.EMPTY_RESPONSE
import retrofit2.Response
import ru.qwonix.android.foxwhiskers.dto.AuthenticationRequestDTO
import ru.qwonix.android.foxwhiskers.entity.UserProfile
import ru.qwonix.android.foxwhiskers.service.AuthenticationService
import ru.qwonix.android.foxwhiskers.service.LocalTokenStorageService
import ru.qwonix.android.foxwhiskers.service.LocalUserStorageService
import javax.inject.Inject


class AuthenticationRepository @Inject constructor(
    private val authenticationService: AuthenticationService,
    private val localUserStorageService: LocalUserStorageService,
    private val localTokenStorageService: LocalTokenStorageService

) {
    fun authenticate(phoneNumber: String, code: Int) = apiRequestFlow {
        val authenticationRequestDTO = AuthenticationRequestDTO(phoneNumber, code)
        val authenticate = authenticationService.authenticate(authenticationRequestDTO)
        val body = authenticate.body()
        if (authenticate.isSuccessful && body != null) {
            localTokenStorageService.saveAccessToken(body.jwtAccessToken)
            localTokenStorageService.saveRefreshToken(body.jwtRefreshToken)
            val userProfile = UserProfile(null, null, null, phoneNumber)
            localUserStorageService.saveUserProfile(userProfile)
        }

        return@apiRequestFlow authenticate
    }

    fun loadUserProfile() = apiRequestFlow {
        val userProfile: UserProfile? = localUserStorageService.loadUserProfile()
        if (userProfile != null) {
            Response.success(userProfile)
        } else {
            Response.error(404, EMPTY_RESPONSE)
        }
    }

    fun sendAuthenticationCode(phoneNumber: String) = apiRequestFlow {
        authenticationService.sendCode(phoneNumber)
    }
}