package ru.qwonix.android.foxwhiskers.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import ru.qwonix.android.foxwhiskers.dto.AuthenticationRequestDTO
import ru.qwonix.android.foxwhiskers.dto.AuthenticationResponseDTO

interface AuthenticationService {

    @POST("auth/authenticate")
    suspend fun authenticate(
        @Body authenticationRequestDTO: AuthenticationRequestDTO
    ): Response<AuthenticationResponseDTO>

    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body refreshToken: String
    ): Response<AuthenticationResponseDTO>

    @POST("auth/code")
    suspend fun sendCode(
        @Body phoneNumber: String
    ): Response<Boolean>

}

