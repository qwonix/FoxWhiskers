package ru.qwonix.android.foxwhiskers.service

import ru.qwonix.android.foxwhiskers.entity.UserProfile
import ru.qwonix.android.foxwhiskers.repository.ResponseDao

interface LocalStorageService {
    suspend fun loadUserProfile(): ResponseDao<UserProfile?>
    suspend fun saveUserProfile(userProfile: UserProfile)
    suspend fun clearUserProfile()
}