package ru.qwonix.android.foxwhiskers.dto

data class UpdateClientDTO(
    val phoneNumber: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?
)
