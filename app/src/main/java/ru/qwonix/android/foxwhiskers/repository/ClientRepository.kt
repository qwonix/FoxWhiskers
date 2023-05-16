package ru.qwonix.android.foxwhiskers.repository

import retrofit2.Response
import ru.qwonix.android.foxwhiskers.dto.UpdateClientDTO
import ru.qwonix.android.foxwhiskers.service.LocalTokenStorageService
import ru.qwonix.android.foxwhiskers.service.LocalClientService
import ru.qwonix.android.foxwhiskers.service.ClientService
import javax.inject.Inject

class ClientRepository @Inject constructor(
    private val clientService: ClientService,
    private val localClientService: LocalClientService,
    private val localTokenStorageService: LocalTokenStorageService
) {

    fun update(updateUserProfileDTO: UpdateClientDTO) = apiRequestFlow {
        val update = clientService.update(updateUserProfileDTO)
        val body = update.body()
        if (update.isSuccessful && body != null) {
            localClientService.saveUserProfile(body)
        }
        return@apiRequestFlow update
    }

    fun logout() = apiRequestFlow {
        localClientService.clearUserProfile()
        localTokenStorageService.clearAccessToken()
        localTokenStorageService.clearRefreshToken()
        Response.success(200)
    }
}