package com.example.weathersnap.di

import com.example.weathersnap.data.GeocodingApiService
import com.example.weathersnap.data.WeatherApiService
import com.example.weathersnap.data.repository.WeatherRepository
import com.example.weathersnap.data.repository.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val GEOCODING_BASE_URL = "https://geocoding-api.open-meteo.com/"
    private const val WEATHER_BASE_URL   = "https://api.open-meteo.com/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    @Named("geocoding")
    fun provideGeocodingRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(GEOCODING_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Named("weather")
    fun provideWeatherRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(WEATHER_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideGeocodingApiService(
        @Named("geocoding") retrofit: Retrofit
    ): GeocodingApiService = retrofit.create(GeocodingApiService::class.java)

    @Provides
    @Singleton
    fun provideWeatherApiService(
        @Named("weather") retrofit: Retrofit
    ): WeatherApiService = retrofit.create(WeatherApiService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        impl: WeatherRepositoryImpl
    ): WeatherRepository
}