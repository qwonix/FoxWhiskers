package ru.qwonix.android.foxwhiskers

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.qwonix.android.foxwhiskers.repository.AuthenticationRepository
import ru.qwonix.android.foxwhiskers.repository.ClientRepository
import ru.qwonix.android.foxwhiskers.repository.MenuRepository
import ru.qwonix.android.foxwhiskers.repository.OrderRepository
import ru.qwonix.android.foxwhiskers.service.AuthenticationService
import ru.qwonix.android.foxwhiskers.service.ClientService
import ru.qwonix.android.foxwhiskers.service.LocalClientService
import ru.qwonix.android.foxwhiskers.service.LocalSettingsService
import ru.qwonix.android.foxwhiskers.service.LocalTokenStorageService
import ru.qwonix.android.foxwhiskers.service.MenuService
import ru.qwonix.android.foxwhiskers.service.OrderService
import ru.qwonix.android.foxwhiskers.util.AuthenticationAuthenticator
import ru.qwonix.android.foxwhiskers.util.AuthenticationInterceptor
import javax.inject.Singleton

val Context.authenticationDataStore by preferencesDataStore(name = "AuthenticationDataStore")
val Context.clientDataStore by preferencesDataStore(name = "Client")
val Context.settingsDataStore by preferencesDataStore(name = "Settings")

@[Module InstallIn(SingletonComponent::class)]
object LocalServiceModule {
    @Provides
    @Singleton
    fun provideLocalClientService(
        @ApplicationContext context: Context
    ) = LocalClientService(context)

    @Provides
    @Singleton
    fun provideLocalTokenStorageService(
        @ApplicationContext context: Context
    ) = LocalTokenStorageService(context)

    @Singleton
    @Provides
    fun provideLocalSettingsService(
        @ApplicationContext context: Context
    ): LocalSettingsService =
        LocalSettingsService(context)
}


@[Module InstallIn(SingletonComponent::class)]
object NetworkServiceModule {

    @Singleton
    @Provides
    fun provideClientService(
        okHttpClient: OkHttpClient,
        retrofit: Retrofit.Builder
    ): ClientService =
        retrofit
            .client(okHttpClient)
            .build()
            .create(ClientService::class.java)

    @Singleton
    @Provides
    fun provideOrderService(
        okHttpClient: OkHttpClient,
        retrofit: Retrofit.Builder
    ): OrderService =
        retrofit
            .client(okHttpClient)
            .build()
            .create(OrderService::class.java)

    @Singleton
    @Provides
    fun provideAuthenticationService(
        okHttpClient: OkHttpClient,
        retrofit: Retrofit.Builder
    ): AuthenticationService =
        retrofit
            .client(okHttpClient)
            .build()
            .create(AuthenticationService::class.java)


    @Singleton
    @Provides
    fun provideMenuService(
        retrofit: Retrofit.Builder
    ): MenuService =
        retrofit
            .build()
            .create(MenuService::class.java)
}

@[Module InstallIn(SingletonComponent::class)]
object RepositoryModule {


    @Provides
    @Singleton
    fun provideAuthenticationRepository(
        clientService: ClientService,
        authenticationService: AuthenticationService,
        localClientService: LocalClientService,
        localTokenStorageService: LocalTokenStorageService

    ) =
        AuthenticationRepository(
            clientService,
            authenticationService,
            localClientService,
            localTokenStorageService
        )

    @Provides
    @Singleton
    fun provideClientRepository(
        clientService: ClientService,
        localClientService: LocalClientService,
        localTokenStorageService: LocalTokenStorageService
    ) =
        ClientRepository(clientService, localClientService, localTokenStorageService)

    @Provides
    @Singleton
    fun provideOrderRepository(
        orderService: OrderService
    ) =
        OrderRepository(orderService)


    @Provides
    @Singleton
    fun provideMenuRepository(menuService: MenuService) =
        MenuRepository(menuService)

}

@[Module InstallIn(SingletonComponent::class)]
object RetrofitModule {

    @Provides
    @Singleton
    fun provideBaseUrl(): String = BuildConfig.FOX_WHISKERS_API_URL

    @Provides
    @Singleton
    fun provideConverterFactory(): Converter.Factory = GsonConverterFactory.create()

    @Provides
    @Singleton
    fun provideRetrofitBuilder(
        BASE_URL: String,
        converterFactory: Converter.Factory
    ): Retrofit.Builder = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(converterFactory)
}

@[Module InstallIn(SingletonComponent::class)]
object OkHttpModule {

    @Singleton
    @Provides
    fun provideAuthenticationInterceptor(localTokenStorageService: LocalTokenStorageService): AuthenticationInterceptor =
        AuthenticationInterceptor(localTokenStorageService)

    @Singleton
    @Provides
    fun provideAuthenticationAuthenticator(
        localTokenStorageService: LocalTokenStorageService,
        retrofit: Retrofit.Builder
    ): AuthenticationAuthenticator {

        val authenticationService = retrofit
            .build()
            .create(AuthenticationService::class.java)

        return AuthenticationAuthenticator(localTokenStorageService, authenticationService)
    }

    @Singleton
    @Provides
    fun provideAuthenticationOkHttpClient(
        authInterceptor: AuthenticationInterceptor,
        authAuthenticator: AuthenticationAuthenticator,
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(authAuthenticator)
            .build()
    }
}