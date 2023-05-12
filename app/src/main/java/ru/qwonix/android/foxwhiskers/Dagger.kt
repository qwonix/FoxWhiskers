package ru.qwonix.android.foxwhiskers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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
import ru.qwonix.android.foxwhiskers.repository.MenuRepository
import ru.qwonix.android.foxwhiskers.repository.UserRepository
import ru.qwonix.android.foxwhiskers.service.AuthenticationService
import ru.qwonix.android.foxwhiskers.service.LocalTokenStorageService
import ru.qwonix.android.foxwhiskers.service.LocalUserStorageService
import ru.qwonix.android.foxwhiskers.service.MenuService
import ru.qwonix.android.foxwhiskers.service.UserService
import ru.qwonix.android.foxwhiskers.util.AuthenticationAuthenticator
import ru.qwonix.android.foxwhiskers.util.AuthenticationInterceptor
import javax.inject.Singleton

val Context.authenticationDataStore: DataStore<Preferences> by preferencesDataStore(name = "AuthenticationDataStore")
val Context.dataStore by preferencesDataStore(name = "UserProfile")

@[Module InstallIn(SingletonComponent::class)]
object ServiceModule {

    @Singleton
    @Provides
    fun provideUserService(
        okHttpClient: OkHttpClient,
        retrofit: Retrofit.Builder
    ): UserService =
        retrofit
            .client(okHttpClient)
            .build()
            .create(UserService::class.java)

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

    @Provides
    @Singleton
    fun provideLocalUserStorageService(
        @ApplicationContext context: Context
    ) = LocalUserStorageService(context)

    @Provides
    @Singleton
    fun provideLocalTokenStorageService(
        @ApplicationContext context: Context
    ) = LocalTokenStorageService(context)


}

@[Module InstallIn(SingletonComponent::class)]
object RepositoryModule {


    @Provides
    @Singleton
    fun provideAuthenticationRepository(
        userService: UserService,
        authenticationService: AuthenticationService,
        localUserStorageService: LocalUserStorageService,
        localTokenStorageService: LocalTokenStorageService

    ) =
        AuthenticationRepository(
            userService,
            authenticationService,
            localUserStorageService,
            localTokenStorageService
        )

    @Provides
    @Singleton
    fun provideUserRepository(
        userService: UserService,
        localUserStorageService: LocalUserStorageService,
        localTokenStorageService: LocalTokenStorageService
    ) =
        UserRepository(userService, localUserStorageService, localTokenStorageService)

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