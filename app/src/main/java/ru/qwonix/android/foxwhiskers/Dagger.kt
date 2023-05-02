package ru.qwonix.android.foxwhiskers

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.qwonix.android.foxwhiskers.repository.AuthenticationRepository
import ru.qwonix.android.foxwhiskers.repository.impl.AuthenticationRepositoryImpl
import ru.qwonix.android.foxwhiskers.service.AuthenticationService
import ru.qwonix.android.foxwhiskers.service.LocalStorageService
import ru.qwonix.android.foxwhiskers.service.impl.AuthenticationServiceImpl
import ru.qwonix.android.foxwhiskers.service.impl.LocalStorageServiceImpl

@[Module InstallIn(SingletonComponent::class)]
class NetworkModule {

    @Provides
    fun provideAuthenticationService(
        authenticationRepository: AuthenticationRepository
    ): AuthenticationService {
        return AuthenticationServiceImpl(authenticationRepository)
    }

    @Provides
    fun provideLocalStorageService(
        @ApplicationContext context: Context
    ): LocalStorageService {
        return LocalStorageServiceImpl(context)
    }

    @Provides
    fun provideAuthenticationRepository(): AuthenticationRepository {
        return AuthenticationRepositoryImpl()
    }
}

