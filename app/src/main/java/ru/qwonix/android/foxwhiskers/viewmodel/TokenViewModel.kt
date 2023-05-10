package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.qwonix.android.foxwhiskers.service.LocalTokenStorageService
import javax.inject.Inject

@HiltViewModel
class TokenViewModel @Inject constructor(
    private val localTokenStorageService: LocalTokenStorageService,
) : ViewModel() {

    val token = MutableLiveData<String?>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            localTokenStorageService.getAccessToken().collect {
                withContext(Dispatchers.Main) {
                    token.value = it
                }
            }
        }
    }

    fun saveAccessToken(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            localTokenStorageService.saveAccessToken(accessToken)
        }
    }

    fun saveRefreshToken(refreshToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            localTokenStorageService.saveRefreshToken(refreshToken)
        }
    }

    fun deleteAccessToken() {
        viewModelScope.launch(Dispatchers.IO) {
            localTokenStorageService.deleteAccessToken()
        }
    }

    fun deleteRefreshToken() {
        viewModelScope.launch(Dispatchers.IO) {
            localTokenStorageService.deleteRefreshToken()
        }
    }
}