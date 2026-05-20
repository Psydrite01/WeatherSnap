package com.example.weathersnap.data

import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApiService {

    @GET("v1/search")
    suspend fun searchCity(
        @Query("name")    name: String,
        @Query("count")   count: Int = 10,
        @Query("language") language: String = "en",
        @Query("format")  format: String = "json"
    ): GeocodingResponse
}


interface WeatherApiService {

    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude")              latitude: Double,
        @Query("longitude")             longitude: Double,
        @Query("current")               current: String = CURRENT_PARAMS,
        @Query("daily")                 daily: String = DAILY_PARAMS,
        @Query("hourly")                hourly: String = HOURLY_PARAMS,
        @Query("forecast_days")         forecastDays: Int = 7,
        @Query("timezone")              timezone: String = "auto"
    ): WeatherResponse

    companion object {
        private const val CURRENT_PARAMS =
            "temperature_2m,relative_humidity_2m,apparent_temperature," +
                    "weather_code,wind_speed_10m,precipitation,is_day"

        private const val DAILY_PARAMS =
            "weather_code,temperature_2m_max,temperature_2m_min,precipitation_sum"

        private const val HOURLY_PARAMS =
            "temperature_2m,weather_code"
    }
}