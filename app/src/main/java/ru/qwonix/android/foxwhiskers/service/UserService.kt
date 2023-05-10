package ru.qwonix.android.foxwhiskers.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import ru.qwonix.android.foxwhiskers.dto.UpdateUserProfileDTO
import ru.qwonix.android.foxwhiskers.entity.UserProfile

interface UserService {
    @GET("auth/user")
    suspend fun find(): Response<UserProfile>

    @POST("auth/update")
    suspend fun update(
        @Body updateUserProfileDTO: UpdateUserProfileDTO
    ): Response<UserProfile>
}