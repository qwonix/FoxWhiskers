package ru.qwonix.android.foxwhiskers.repository

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Response


sealed class ApiResponse<out T> {
    object Loading : ApiResponse<Nothing>()

    data class Success<out T>(
        val data: T
    ) : ApiResponse<T>()

    data class Failure(
        val errorMessage: String,
        val code: Int,
    ) : ApiResponse<Nothing>()
}


data class ErrorResponse(
    val status: Int,
    val error: String
)

fun <T> requestFlow(call: suspend () -> T): Flow<ApiResponse<T>> = flow {
    emit(ApiResponse.Loading)

    withTimeoutOrNull(20000L) {
        val response = call()

        try {
            if (response != null) {
                emit(ApiResponse.Success(response))
            } else {
                emit(ApiResponse.Failure("response is null", 400))
            }
        } catch (e: Exception) {
            emit(ApiResponse.Failure(e.message ?: e.toString(), 400))
        }
    } ?: emit(ApiResponse.Failure("Timeout! Please try again.", 408))
}.flowOn(Dispatchers.IO)

fun <T> apiRequestFlow(call: suspend () -> Response<T>): Flow<ApiResponse<T>> = flow {
    emit(ApiResponse.Loading)

    withTimeoutOrNull(20000L) {
        val response = call()

        try {
            if (response.isSuccessful) {
                response.body()?.let { data ->
                    emit(ApiResponse.Success(data))
                }
            } else {
                response.errorBody()?.let { error ->
                    error.close()
                    val fromJson = Gson().fromJson(error.charStream(), ErrorResponse::class.java)
                    if (fromJson == null) {
                        emit(ApiResponse.Failure("response error message parsing error", 400))
                    } else {
                        val parsedError: ErrorResponse = fromJson
                        emit(ApiResponse.Failure(parsedError.error, parsedError.status))
                    }
                }
            }
        } catch (e: Exception) {
            emit(ApiResponse.Failure(e.message ?: e.toString(), 400))
        }
    } ?: emit(ApiResponse.Failure("Timeout! Please try again.", 408))
}.flowOn(Dispatchers.IO)