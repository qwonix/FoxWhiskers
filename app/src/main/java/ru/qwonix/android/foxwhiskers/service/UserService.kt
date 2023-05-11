package ru.qwonix.android.foxwhiskers.service

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import ru.qwonix.android.foxwhiskers.dto.UpdateUserProfileDTO
import ru.qwonix.android.foxwhiskers.entity.UserProfile

interface UserService {
    @PUT("client/update")
    suspend fun update(
        @Body updateUserProfileDTO: UpdateUserProfileDTO
    ): Response<UserProfile>
}