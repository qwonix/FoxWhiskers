package ru.qwonix.android.foxwhiskers.repository

import okhttp3.internal.EMPTY_RESPONSE
import retrofit2.Response
import ru.qwonix.android.foxwhiskers.dto.AuthenticationRequestDTO
import ru.qwonix.android.foxwhiskers.entity.Client
import ru.qwonix.android.foxwhiskers.service.AuthenticationService
import ru.qwonix.android.foxwhiskers.service.ClientService
import ru.qwonix.android.foxwhiskers.service.LocalClientService
import ru.qwonix.android.foxwhiskers.service.LocalTokenStorageService
import javax.inject.Inject


class AuthenticationRepository @Inject constructor(
    private val clientService: ClientService,
    private val authenticationService: AuthenticationService,
    private val localClientService: LocalClientService,
    private val localTokenStorageService: LocalTokenStorageService

) {
    fun authenticate(phoneNumber: String, code: Int) = apiRequestFlow {
        val authenticationRequestDTO = AuthenticationRequestDTO(phoneNumber, code)
        val authenticate = authenticationService.authenticate(authenticationRequestDTO)
        val body = authenticate.body()
        if (authenticate.isSuccessful && body != null) {
            localTokenStorageService.saveAccessToken(body.jwtAccessToken)
            localTokenStorageService.saveRefreshToken(body.jwtRefreshToken)
            val client = Client(phoneNumber)
            localClientService.saveUserProfile(client)
        }

        return@apiRequestFlow authenticate
    }

    fun loadClient() = apiRequestFlow {
        var loadedClient: Client? = localClientService.loadUserProfile()
        if (loadedClient != null) {
            val response = clientService.one(loadedClient.phoneNumber)
            val body = response.body()
            if (response.isSuccessful && body != null) {
                if (body != loadedClient)
                    loadedClient = body
            }
            else {
                return@apiRequestFlow response
            }
        } else {
            return@apiRequestFlow Response.error(404, EMPTY_RESPONSE)
        }
        return@apiRequestFlow Response.success(loadedClient)
    }

    fun sendAuthenticationCode(phoneNumber: String) = apiRequestFlow {
        authenticationService.sendCode(phoneNumber)
    }
}