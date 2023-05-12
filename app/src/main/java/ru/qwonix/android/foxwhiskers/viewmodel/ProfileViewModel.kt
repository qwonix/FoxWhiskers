package ru.qwonix.android.foxwhiskers.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
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

    private val TAG = "ProfileViewModel"

    private val _authenticatedClient = MutableLiveData<Client>()
    val authenticatedClient: LiveData<Client> = _authenticatedClient

    private val _clientAuthenticationResponse = MutableLiveData<ApiResponse<Client?>>()
    val clientAuthenticationResponse: LiveData<ApiResponse<Client?>> = _clientAuthenticationResponse

    private val _clientUpdateResponse = MutableLiveData<ApiResponse<Client?>>()
    val clientUpdateResponse: LiveData<ApiResponse<Client?>> = _clientUpdateResponse


    init {
        clientAuthenticationResponse.observeForever {
            when (it) {
                is ApiResponse.Success -> {
                    Log.i(TAG, "${it} ${it.data}")
                    val data = it.data!!
                    _authenticatedClient.postValue(data)
                }

                else -> {}
            }
        }
    }

    fun tryLoadClient(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _clientAuthenticationResponse,
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
        _clientUpdateResponse,
        coroutinesErrorHandler
    ) {
        val updateClientDTO = UpdateClientDTO(phoneNumber, firstName, lastName, email)
        userRepository.update(updateClientDTO)
    }


    fun logout(coroutinesErrorHandler: CoroutinesErrorHandler) {
        baseRequest(MutableLiveData(), coroutinesErrorHandler) {
            userRepository.logout()
        }
        _clientAuthenticationResponse.postValue(ApiResponse.Failure("logout", 401))
    }

    fun isRequiredForEdit(data: Client): Boolean {
        return data.firstName.isNullOrBlank() || data.lastName.isNullOrBlank() || data.email.isNullOrBlank()
    }
}