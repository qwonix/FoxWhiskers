package ru.qwonix.android.foxwhiskers.repository

import retrofit2.Response
import ru.qwonix.android.foxwhiskers.dto.UpdateUserProfileDTO
import ru.qwonix.android.foxwhiskers.service.LocalTokenStorageService
import ru.qwonix.android.foxwhiskers.service.LocalUserStorageService
import ru.qwonix.android.foxwhiskers.service.UserService
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService,
    private val localUserStorageService: LocalUserStorageService,
    private val localTokenStorageService: LocalTokenStorageService
) {



    fun update(updateUserProfileDTO: UpdateUserProfileDTO) = apiRequestFlow {
        val update = userService.update(updateUserProfileDTO)
        val body = update.body()
        if (update.isSuccessful && body != null) {
            localUserStorageService.saveUserProfile(body)
        }
        return@apiRequestFlow update
    }

    fun logout() = apiRequestFlow {
        localUserStorageService.clearUserProfile()
        localTokenStorageService.clearAccessToken()
        localTokenStorageService.clearRefreshToken()
        Response.success(200)
    }
}