package ru.qwonix.android.foxwhiskers.entity

data class UserProfile(
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String,
    val jwtAccessToken: String,
    val jwtRefreshToken: String
)