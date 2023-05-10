package ru.qwonix.android.foxwhiskers.dto

data class AuthenticationRequestDTO(
    val phoneNumber: String,
    val code: Int
)
