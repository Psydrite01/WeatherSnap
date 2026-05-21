package com.example.weathersnap.data.repository

import com.example.weathersnap.data.GeocodingApiService
import com.example.weathersnap.data.WeatherApiService
import com.example.weathersnap.data.local.CachedCityEntity
import com.example.weathersnap.data.local.CityDao
import com.example.weathersnap.domain.CityResult
import com.example.weathersnap.domain.WeatherInfo
import com.example.weathersnap.domain.toDomain
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    private val weatherApi: WeatherApiService,
    private val cityDao: CityDao,
    private val gson: Gson
) : WeatherRepository {

    companion object {
        private const val CACHE_TTL_MS = 24 * 60 * 60 * 1000L  // 24 hours
    }

    override suspend fun searchCity(query: String): Result<List<CityResult>> {
        val key = query.trim().lowercase()

        // 1. Check cache
        val cached = cityDao.getByQuery(key)
        if (cached != null && System.currentTimeMillis() - cached.cachedAt < CACHE_TTL_MS) {
            val type = object : TypeToken<List<CityResult>>() {}.type
            return Result.success(gson.fromJson(cached.citiesJson, type))
        }

        // 2. Cache miss → hit network
        return try {
            val response = geocodingApi.searchCity(name = query)
            val cities = response.results?.map { it.toDomain() } ?: emptyList()

            // 3. Store result
            cityDao.insert(
                CachedCityEntity(
                    query = key,
                    citiesJson = gson.toJson(cities),
                    cachedAt = System.currentTimeMillis()
                )
            )

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