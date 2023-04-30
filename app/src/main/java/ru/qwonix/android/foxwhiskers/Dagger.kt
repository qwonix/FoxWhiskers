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
import ru.qwonix.android.foxwhiskers.service.impl.AuthenticationServiceImpl

@[Module InstallIn(SingletonComponent::class)]
class NetworkModule {

    @Provides
    fun provideAuthenticationService(
        @ApplicationContext context: Context,
        authenticationRepository: AuthenticationRepository
    ): AuthenticationService {
        return AuthenticationServiceImpl(context, authenticationRepository)
    }

    @Provides
    fun provideAuthenticationRepository(): AuthenticationRepository {

        return AuthenticationRepositoryImpl()
    }
}

