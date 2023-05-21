package ru.qwonix.android.foxwhiskers.repository

import retrofit2.Response
import ru.qwonix.android.foxwhiskers.dao.MenuItem
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.service.LocalCartService
import ru.qwonix.android.foxwhiskers.service.MenuService

class MenuCartRepository(
    private val menuService: MenuService,
    private val localCartService: LocalCartService
) {

    fun changeDishCount(dish: Dish, newCount: Int) = requestFlow {
        val dishesInCart = localCartService.getDishesInCart().toMutableList()

        val find = dishesInCart.find { it.id == dish.id }

        when {
            newCount == 1 && dish !in dishesInCart -> dishesInCart += dish
            newCount == 0 -> dishesInCart -= dish
            find != null -> find.count = newCount
        }
        localCartService.save(dishesInCart)
        return@requestFlow localCartService.getDishesInCart()
    }

    fun getDishesInCart() = requestFlow {
        localCartService.getDishesInCart()
    }

    fun clear() = requestFlow {
        localCartService.clear()
    }

    fun getAllDishes() = apiRequestFlow {
        val menuItemsResponse = menuService.findAllDishes()
        val cartDishes = localCartService.getDishesInCart()

        val menuItems: List<MenuItem> =
            menuItemsResponse.body() ?: return@apiRequestFlow menuItemsResponse

        // set count to the same as in the cart
        for (item in menuItems) {
            for (dish in item.items) {
                for (cartDish in cartDishes) {
                    if (dish == cartDish) {
                        dish.count = cartDish.count
                    }
                }
            }
        }
        return@apiRequestFlow Response.success(menuItems)

    }
}