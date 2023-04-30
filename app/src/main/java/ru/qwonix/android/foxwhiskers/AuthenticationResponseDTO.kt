package ru.qwonix.android.foxwhiskers

data class AuthenticationResponseDTO(
    val jwtAccessToken: String,
    val jwtRefreshToken: String
)
