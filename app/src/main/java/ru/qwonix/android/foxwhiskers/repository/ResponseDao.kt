package ru.qwonix.android.foxwhiskers.repository

data class ResponseDao<T : Any>(
    val data: T,
    val isSuccessful: Boolean,
    val code: Int,
    val message: String
) {
    companion object {
        fun <T : Any> ofSuccess(data: T): ResponseDao<T> {
            return ResponseDao(data, true, 1, "Success")
        }
    }
}
