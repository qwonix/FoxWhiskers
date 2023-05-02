package ru.qwonix.android.foxwhiskers.retrofit

import retrofit2.Response
import retrofit2.http.POST
import ru.qwonix.android.foxwhiskers.AuthenticationResponseDTO
import ru.qwonix.android.foxwhiskers.entity.UserProfile

interface AuthenticationRepository {

    @POST("user")
    suspend fun findUserProfile(
        phoneNumber: String,
        jwtAccessToken: String
    ): Response<UserProfile>

    @POST("dish")
    suspend fun sendAuthenticationSmsCodeToNumber(phoneNumber: String): Response<Boolean>

    @POST("dish")
    suspend fun authenticate(
        phoneNumber: String,
        code: Int
    ): Response<AuthenticationResponseDTO?>

    @POST("dish")
    suspend fun updateProfile(userProfile: UserProfile): Response<UserProfile?>
}