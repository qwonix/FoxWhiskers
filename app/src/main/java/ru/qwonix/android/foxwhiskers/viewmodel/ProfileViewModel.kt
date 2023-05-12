package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.qwonix.android.foxwhiskers.dto.UpdateClientDTO
import ru.qwonix.android.foxwhiskers.entity.Client
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.repository.AuthenticationRepository
import ru.qwonix.android.foxwhiskers.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authenticationRepository: AuthenticationRepository
) : BaseViewModel() {

    private val _authenticatedClient = MutableLiveData<ApiResponse<Client?>>()
    val authenticatedClient = _authenticatedClient

    private val _updatedClient = MutableLiveData<ApiResponse<Client?>>()
    val updatedClient = _updatedClient

    fun tryLoadClient(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _authenticatedClient,
        coroutinesErrorHandler
    ) {
        authenticationRepository.loadClient()
    }

    fun update(
        phoneNumber: String,
        firstName: String,
        lastName: String,
        email: String,
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _updatedClient,
        coroutinesErrorHandler
    ) {
        val updateClientDTO = UpdateClientDTO(phoneNumber, firstName, lastName, email)
        userRepository.update(updateClientDTO)
    }


    fun logout(coroutinesErrorHandler: CoroutinesErrorHandler) = baseRequest(
        MutableLiveData(),
        coroutinesErrorHandler
    ) {
        userRepository.logout()
    }

    fun isRequiredForEdit(data: Client): Boolean {
        return data.firstName.isNullOrBlank() || data.lastName.isNullOrBlank() || data.email.isNullOrBlank()
    }
}