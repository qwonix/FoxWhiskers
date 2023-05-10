package ru.qwonix.android.foxwhiskers.util

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import ru.qwonix.android.foxwhiskers.service.LocalTokenStorageService
import javax.inject.Inject

class AuthenticationInterceptor @Inject constructor(
    private val localTokenStorageService: LocalTokenStorageService,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            localTokenStorageService.getAccessToken().first()
        }
        val request = chain.request().newBuilder()
        if (token != null) {
            request.addHeader("Authorization", token)
        }
        return chain.proceed(request.build())
    }
}