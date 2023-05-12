package ru.qwonix.android.foxwhiskers.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import ru.qwonix.android.foxwhiskers.dto.UpdateUserProfileDTO
import ru.qwonix.android.foxwhiskers.entity.UserProfile

interface UserService {

    @POST("client")
    suspend fun one(@Body phoneNumber: String): Response<UserProfile>

    @PUT("client/update")
    suspend fun update(
        @Body updateUserProfileDTO: UpdateUserProfileDTO
    ): Response<UserProfile>
}