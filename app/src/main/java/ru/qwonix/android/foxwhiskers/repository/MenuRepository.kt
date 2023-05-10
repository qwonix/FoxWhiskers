package ru.qwonix.android.foxwhiskers.repository

import ru.qwonix.android.foxwhiskers.service.MenuService

class MenuRepository(
    private val menuService: MenuService
) {

    fun findAllDishes() = apiRequestFlow {
        menuService.findAllDishes()
    }

    fun findAllLocations() = apiRequestFlow {
        menuService.findAllLocations()
    }
}