package ru.qwonix.android.foxwhiskers.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Client(
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phoneNumber: String
) : Parcelable