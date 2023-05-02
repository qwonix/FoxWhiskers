package ru.qwonix.android.foxwhiskers.service.impl

import ru.qwonix.android.foxwhiskers.AuthenticationResponseDTO
import ru.qwonix.android.foxwhiskers.entity.UserProfile
import ru.qwonix.android.foxwhiskers.repository.AuthenticationRepository
import ru.qwonix.android.foxwhiskers.repository.ResponseDao
import ru.qwonix.android.foxwhiskers.service.AuthenticationService

class AuthenticationServiceImpl(
    private val authenticationRepository: AuthenticationRepository
) :
    AuthenticationService {

    override suspend fun updateProfile(userProfile: UserProfile): ResponseDao<UserProfile?> {
        return authenticationRepository.updateProfile(userProfile)
    }

    override suspend fun findUserProfile(
        phoneNumber: String,
        jwtAccessToken: String
    ): ResponseDao<UserProfile?> {
        return authenticationRepository.loadUserProfile(phoneNumber, jwtAccessToken)
    }

    override suspend fun authenticate(
        phoneNumber: String,
        code: Int
    ): ResponseDao<AuthenticationResponseDTO?> {
        return authenticationRepository.authenticate(phoneNumber, code)
    }

    override suspend fun sendAuthenticationSmsCodeToNumber(phoneNumber: String) {
        authenticationRepository.sendAuthenticationSmsCodeToNumber(phoneNumber)
    }
}
