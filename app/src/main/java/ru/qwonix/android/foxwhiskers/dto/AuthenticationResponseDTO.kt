package ru.qwonix.android.foxwhiskers.dto

data class AuthenticationResponseDTO(
    val jwtAccessToken: String,
    val jwtRefreshToken: String
)
