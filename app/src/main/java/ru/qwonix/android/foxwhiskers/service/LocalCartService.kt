package ru.qwonix.android.foxwhiskers.service

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.firstOrNull
import ru.qwonix.android.foxwhiskers.entity.Dish
import ru.qwonix.android.foxwhiskers.menuDataStore


class LocalCartService(private val gson: Gson, private val context: Context) {

    private companion object {
        val CART = stringPreferencesKey("CART")
    }

    suspend fun getDishesInCart(): List<Dish> {
        val preferences = context.menuDataStore.data.firstOrNull()
        return if (preferences == null || !preferences.contains(CART)) {
            emptyList()
        } else {
            val type = object : TypeToken<List<Dish>>() {}.type
            return gson.fromJson(preferences[CART], type)
        }
    }

    suspend fun save(dishes: Collection<Dish>) {
        context.menuDataStore.edit { preferences ->
            preferences[CART] = gson.toJson(dishes)
        }
    }

    suspend fun clear() {
        context.menuDataStore.edit { preferences -> preferences.clear() }
    }

}