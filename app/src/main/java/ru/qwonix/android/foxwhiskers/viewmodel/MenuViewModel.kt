package ru.qwonix.android.foxwhiskers.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.qwonix.android.foxwhiskers.dao.MenuItem
import ru.qwonix.android.foxwhiskers.repository.ApiResponse
import ru.qwonix.android.foxwhiskers.repository.MenuCartRepository
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuCartRepository: MenuCartRepository
) : BaseViewModel() {
    private val TAG = "MenuViewModel"

    private val _menu: MutableLiveData<ApiResponse<List<MenuItem>>> = MutableLiveData()
    val menu: LiveData<ApiResponse<List<MenuItem>>> = _menu

    fun loadDishes(
        coroutinesErrorHandler: CoroutinesErrorHandler
    ) = baseRequest(
        _menu,
        coroutinesErrorHandler
    ) {
        menuCartRepository.getAllDishes()
    }

}
