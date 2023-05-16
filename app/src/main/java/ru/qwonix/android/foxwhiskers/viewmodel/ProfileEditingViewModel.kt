package ru.qwonix.android.foxwhiskers.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.qwonix.android.foxwhiskers.dto.UpdateClientDTO
import ru.qwonix.android.foxwhiskers.entity.Client
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.repository.ClientRepository
import javax.inject.Inject

@HiltViewModel
class ProfileEditingViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : BaseViewModel() {

    private val TAG = "ProfileEditingViewModel"

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

}