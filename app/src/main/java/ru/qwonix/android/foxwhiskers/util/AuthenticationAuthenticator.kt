package ru.qwonix.android.foxwhiskers.util

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.qwonix.android.foxwhiskers.service.AuthenticationService
import ru.qwonix.android.foxwhiskers.service.LocalTokenStorageService
import javax.inject.Inject

class AuthenticationAuthenticator @Inject constructor(
    private val localTokenStorageService: LocalTokenStorageService,
    private val authenticationService: AuthenticationService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val refreshToken = runBlocking {
            localTokenStorageService.getRefreshToken().first()
        }
        return runBlocking {
            if (refreshToken == null) {
                localTokenStorageService.clearAccessToken()
                localTokenStorageService.clearRefreshToken()
                return@runBlocking null
            }

            val newToken = authenticationService.refreshToken(refreshToken)

            if (newToken.isSuccessful && newToken.body() == null) { //Couldn't refresh the token, so restart the login process
                localTokenStorageService.clearAccessToken()
                localTokenStorageService.clearRefreshToken()
                return@runBlocking null
            } else {
                newToken.body()?.let {
                    localTokenStorageService.saveAccessToken(it.jwtAccessToken)
                    localTokenStorageService.saveRefreshToken(it.jwtAccessToken)
                    response.request.newBuilder()
                        .header("Authorization", it.jwtAccessToken)
                        .build()
                }
            }
        }
    }
}