package ru.qwonix.android.foxwhiskers.repository

import ru.qwonix.android.foxwhiskers.dto.UpdateUserProfileDTO
import ru.qwonix.android.foxwhiskers.service.UserService
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userService: UserService,
) {
    fun find() = apiRequestFlow {
        userService.find()
    }

    fun update(updateUserProfileDTO: UpdateUserProfileDTO) = apiRequestFlow {
        userService.update(updateUserProfileDTO)
    }
}