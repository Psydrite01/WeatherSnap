package com.example.weathersnap.data.repository

import com.example.weathersnap.data.GeocodingApiService
import com.example.weathersnap.data.WeatherApiService
import com.example.weathersnap.domain.CityResult
import com.example.weathersnap.domain.WeatherInfo
import com.example.weathersnap.domain.toDomain
import javax.inject.Inject

interface WeatherRepository {
    suspend fun searchCity(query: String): Result<List<CityResult>>
    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        cityName: String
    ): Result<WeatherInfo>
}

class WeatherRepositoryImpl @Inject constructor(
    private val geocodingApi: GeocodingApiService,
    private val weatherApi: WeatherApiService
) : WeatherRepository {

    override suspend fun searchCity(query: String): Result<List<CityResult>> {
        return try {
            val response = geocodingApi.searchCity(name = query)
            val cities = response.results?.map { it.toDomain() } ?: emptyList()
            Result.success(cities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        cityName: String
    ): Result<WeatherInfo> {
        return try {
            val response = weatherApi.getWeather(
                latitude = latitude,
                longitude = longitude
            )
            Result.success(response.toDomain(cityName))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}