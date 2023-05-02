package ru.qwonix.android.foxwhiskers.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfile(
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String,
    val jwtAccessToken: String,
    val jwtRefreshToken: String
) : Parcelable {
    fun isRequiredForEdit(): Boolean {
        return firstName.isNullOrBlank() || lastName.isNullOrBlank() || email.isNullOrBlank()
    }
}