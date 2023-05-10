package ru.qwonix.android.foxwhiskers.repository

import retrofit2.Response
import ru.qwonix.android.foxwhiskers.service.LocalTokenStorageService
import javax.inject.Inject

class LocalTokenStorageRepository @Inject constructor(val localTokenStorageService: LocalTokenStorageService) {
    fun saveAccessToken(accessToken: String) = apiRequestFlow {
        // TODO: get response from dataStore
        Response.success(localTokenStorageService.saveAccessToken(accessToken))
    }

    fun saveRefreshToken(refreshToken: String) = apiRequestFlow {
        // TODO: get response from dataStore
        Response.success(localTokenStorageService.saveRefreshToken(refreshToken))
    }

    fun deleteAccessToken() = apiRequestFlow {
        // TODO: get response from dataStore
        Response.success(localTokenStorageService.deleteAccessToken())
    }

    fun deleteRefreshToken() = apiRequestFlow {
        // TODO: get response from dataStore
        Response.success(localTokenStorageService.deleteRefreshToken())
    }

}