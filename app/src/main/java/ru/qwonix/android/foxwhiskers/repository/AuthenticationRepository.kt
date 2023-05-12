package ru.qwonix.android.foxwhiskers.repository

import okhttp3.internal.EMPTY_RESPONSE
import retrofit2.Response
import ru.qwonix.android.foxwhiskers.dto.AuthenticationRequestDTO
import ru.qwonix.android.foxwhiskers.entity.Client
import ru.qwonix.android.foxwhiskers.service.AuthenticationService
import ru.qwonix.android.foxwhiskers.service.LocalTokenStorageService
import ru.qwonix.android.foxwhiskers.service.LocalUserStorageService
import ru.qwonix.android.foxwhiskers.service.UserService
import javax.inject.Inject


class AuthenticationRepository @Inject constructor(
    private val userService: UserService,
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
            val client = Client(null, null, null, phoneNumber)
            localUserStorageService.saveUserProfile(client)
        }

        return@apiRequestFlow authenticate
    }

    fun loadClient() = apiRequestFlow {
        var loadedUserProfile: Client? = localUserStorageService.loadUserProfile()
        if (loadedUserProfile != null) {
            val userProfileResponse = userService.one(loadedUserProfile.phoneNumber)
            val body = userProfileResponse.body()
            if (userProfileResponse.isSuccessful && body != null) {
                if (body != loadedUserProfile)
                    loadedUserProfile = body
            }
        } else {
            return@apiRequestFlow Response.error(404, EMPTY_RESPONSE)
        }
        return@apiRequestFlow Response.success(loadedUserProfile)
    }

    fun sendAuthenticationCode(phoneNumber: String) = apiRequestFlow {
        authenticationService.sendCode(phoneNumber)
    }
}