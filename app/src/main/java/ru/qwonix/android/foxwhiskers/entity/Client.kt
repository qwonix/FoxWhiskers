package ru.qwonix.android.foxwhiskers.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Client(
    val phoneNumber: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?
) : Parcelable {
    constructor(phoneNumber: String) : this(phoneNumber, null, null, null)
}