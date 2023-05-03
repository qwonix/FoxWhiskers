package ru.qwonix.android.foxwhiskers

import android.content.Context
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
import ru.qwonix.android.foxwhiskers.retrofit.AuthenticationRepository
import ru.qwonix.android.foxwhiskers.retrofit.LocalStorageRepository
import ru.qwonix.android.foxwhiskers.retrofit.MenuRepository
import javax.inject.Singleton

@[Module InstallIn(SingletonComponent::class)]
object NetworkModule {

    @Provides
    fun provideBaseUrl(): String = BuildConfig.FOX_WHISKERS_API_URL

    @Provides
    @Singleton
    fun provideInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideClient(interceptor: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder().addInterceptor(interceptor).build()

    @Provides
    @Singleton
    fun provideConverterFactory(): Converter.Factory = GsonConverterFactory.create()


    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient,
        converterFactory: Converter.Factory,
        BASE_URL: String
    ): Retrofit = Retrofit.Builder()
        .addConverterFactory(converterFactory)
        .client(client)
        .baseUrl(BASE_URL)
        .build()

    @Provides
    @Singleton
    fun provideMenuRepository(retrofit: Retrofit): MenuRepository =
        retrofit.create(MenuRepository::class.java)

    @Provides
    fun provideAuthenticationRepository(retrofit: Retrofit): AuthenticationRepository =
        retrofit.create(AuthenticationRepository::class.java)

    @Provides
    fun provideLocalStorageRepository(
        @ApplicationContext context: Context
    ): LocalStorageRepository {
        return LocalStorageRepository(context)
    }
}

