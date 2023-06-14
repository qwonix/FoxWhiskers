package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.qwonix.android.foxwhiskers.entity.PickUpLocation
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.repository.PickUpLocationRepository
import javax.inject.Inject

@HiltViewModel
class PickUpLocationViewModel @Inject constructor(
    private val pickUpLocationRepository: PickUpLocationRepository
) : BaseViewModel() {

    private val _pickUpLocations = MutableLiveData<ApiResponse<List<PickUpLocation>>>()
    val pickUpLocations: LiveData<ApiResponse<List<PickUpLocation>>> = _pickUpLocations

    private val _selectedPickUpLocationResponse = MutableLiveData<ApiResponse<PickUpLocation?>>()
    val selectedPickUpLocationResponse: LiveData<ApiResponse<PickUpLocation?>> = _selectedPickUpLocationResponse

    fun setSelectedPickUpLocation(
        pickUpLocation: PickUpLocation,
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(coroutinesErrorHandler) {
        _selectedPickUpLocationResponse.postValue(ApiResponse.Success(pickUpLocation))
        pickUpLocationRepository.setSelected(pickUpLocation)
    }

    fun tryLoadSelectedPickUpLocation(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _selectedPickUpLocationResponse,
        coroutinesErrorHandler
    ) {
        pickUpLocationRepository.selected()
    }

    fun tryLoadPickUpLocations(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _pickUpLocations,
        coroutinesErrorHandler
    ) {
        pickUpLocationRepository.findAllLocations()
    }
}