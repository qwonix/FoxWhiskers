package ru.qwonix.android.foxwhiskers.repository

import ru.qwonix.android.foxwhiskers.AuthenticationResponseDTO
import ru.qwonix.android.foxwhiskers.entity.UserProfile

interface AuthenticationRepository {
    suspend fun loadUserProfile(
        phoneNumber: String,
        jwtAccessToken: String
    ): ResponseDao<UserProfile?>

    suspend fun sendAuthenticationSmsCodeToNumber(phoneNumber: String): ResponseDao<Boolean>
    suspend fun authenticate(
        phoneNumber: String,
        code: Int
    ): ResponseDao<AuthenticationResponseDTO?>

    suspend fun updateProfile(userProfile: UserProfile): ResponseDao<UserProfile?>
}