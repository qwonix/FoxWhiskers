package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.qwonix.android.foxwhiskers.dto.UpdateClientDTO
import ru.qwonix.android.foxwhiskers.entity.Client
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.repository.AuthenticationRepository
import ru.qwonix.android.foxwhiskers.repository.ClientRepository
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    private val authenticationRepository: AuthenticationRepository
) : BaseViewModel() {

    private val TAG = "ProfileViewModel"

    private val _clientAuthenticationResponse = MutableLiveData<ApiResponse<Client?>>()
    val clientAuthenticationResponse: LiveData<ApiResponse<Client?>> = _clientAuthenticationResponse

    private val _clientUpdateResponse = MutableLiveData<ApiResponse<Client?>>()
    val clientUpdateResponse: LiveData<ApiResponse<Client?>> = _clientUpdateResponse

    fun update(
        phoneNumber: String,
        firstName: String,
        lastName: String,
        email: String,
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _clientUpdateResponse,
        coroutinesErrorHandler
    ) {
        val updateClientDTO = UpdateClientDTO(phoneNumber, firstName, lastName, email)
        clientRepository.update(updateClientDTO)
    }

    fun tryLoadClient(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _clientAuthenticationResponse,
        coroutinesErrorHandler
    ) {
        authenticationRepository.loadClientFromLocalStorage()
    }


    fun logout(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        MutableLiveData(),
        coroutinesErrorHandler
    ) {
        clientRepository.logout()
    }


    fun isRequiredForEdit(data: Client): Boolean {
        return data.firstName.isNullOrBlank() || data.lastName.isNullOrBlank() || data.email.isNullOrBlank()
    }
}