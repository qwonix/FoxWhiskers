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


    private val _selectedPickUpLocation = MutableLiveData<PickUpLocation>()
    val selectedPickUpLocation: LiveData<PickUpLocation> = _selectedPickUpLocation

    private val _pickUpLocations = MutableLiveData<ApiResponse<List<PickUpLocation>>>()
    val pickUpLocations: LiveData<ApiResponse<List<PickUpLocation>>> = _pickUpLocations

    private val _selectedPickUpLocationResponse = MutableLiveData<ApiResponse<PickUpLocation?>>()

    init {
        _selectedPickUpLocationResponse.observeForever {
            if (it is ApiResponse.Success) {
                _selectedPickUpLocation.postValue(it.data!!)
            }
        }

        tryLoadSelectedPickUpLocation(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented")
            }
        })

        tryLoadPickUpLocations(object : CoroutinesErrorHandler {
            override fun onError(message: String) {
                TODO("Not yet implemented")
            }
        })
    }

    fun setPickUpLocation(pickUpLocation: PickUpLocation) {
        _selectedPickUpLocation.postValue(pickUpLocation)
        pickUpLocationRepository.setSelected(pickUpLocation)
    }

    private fun tryLoadSelectedPickUpLocation(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _selectedPickUpLocationResponse, coroutinesErrorHandler
    ) {
        pickUpLocationRepository.selected()
    }

    fun tryLoadPickUpLocations(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _pickUpLocations, coroutinesErrorHandler
    ) {
        pickUpLocationRepository.findAllLocations()
    }
}