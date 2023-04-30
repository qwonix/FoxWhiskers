package ru.qwonix.android.foxwhiskers.repository

data class ResponseDao<T : Any?>(
    val data: T,
    val isSuccessful: Boolean,
    val code: Int,
    val message: String
) {
    companion object {
        fun <T> ofSuccess(data: T): ResponseDao<T> {
            return ResponseDao(data, true, 1, "Success")
        }

        fun <T> ofNullable(data: T): ResponseDao<T?> {
            return if (data == null) {
                ResponseDao(null, false, 404, "Unsuccess")
            } else {
                ResponseDao.ofSuccess(data)
            }
        }
    }
}
