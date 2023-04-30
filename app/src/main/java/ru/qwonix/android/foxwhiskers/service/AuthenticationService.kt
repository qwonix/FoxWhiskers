package ru.qwonix.android.foxwhiskers.service

import ru.qwonix.android.foxwhiskers.AuthenticationResponseDTO
import ru.qwonix.android.foxwhiskers.entity.UserProfile
import ru.qwonix.android.foxwhiskers.repository.ResponseDao

interface AuthenticationService {
    suspend fun loadUserProfile(): UserProfile?

    suspend fun loadUserProfile(
        phoneNumber: String,
        jwtAccessToken: String
    ): ResponseDao<UserProfile?>

    suspend fun authenticate(
        phoneNumber: String,
        code: Int
    ): ResponseDao<AuthenticationResponseDTO?>

    suspend fun sendAuthenticationSmsCodeToNumber(phoneNumber: String)

    suspend fun saveUserProfile(userProfile: UserProfile)

    suspend fun clearUserProfile()
}