package ru.qwonix.android.foxwhiskers.repository

import kotlinx.coroutines.runBlocking
import okhttp3.internal.EMPTY_RESPONSE
import retrofit2.Response
import ru.qwonix.android.foxwhiskers.entity.UserProfile
import ru.qwonix.android.foxwhiskers.service.LocalUserStorageService
import javax.inject.Inject

class LocalUserStorageRepository @Inject constructor(
    private val localUserStorageService: LocalUserStorageService
) {
    fun loadUserProfile() = apiRequestFlow {
        var userProfile: UserProfile?
        runBlocking {
            userProfile = localUserStorageService.loadUserProfile()
        }
        if (userProfile != null) {
            Response.success(userProfile)
        } else {
            Response.error(404, EMPTY_RESPONSE)
        }
    }

    fun saveUserProfile(userProfile: UserProfile) = apiRequestFlow {
        // TODO: get response from dataStore
        Response.success(localUserStorageService.saveUserProfile(userProfile))
    }

    fun clearUserProfile() = apiRequestFlow {
        // TODO: get response from dataStore
        Response.success(localUserStorageService.clearUserProfile())
    }

}