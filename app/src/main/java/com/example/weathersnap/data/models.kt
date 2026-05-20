package com.example.weathersnap.data

import com.google.gson.annotations.SerializedName

data class SavedReport(
    val cityName      : String = "Placeholder City",
    val condition     : String = "Partly cloudy",
    val dateTime      : String = "00 Jan 2026, 00:00 pm",
    val temperature   : String = "00°C",
    val originalSize  : String = "000 KB",
    val compressedSize: String = "00 KB",
    val tag           : String = "placeholder_report"
)


data class GeocodingResponse(
    @SerializedName("results") val results: List<GeocodingResult>? = null
)

data class GeocodingResult(
    @SerializedName("id")          val id: Long,
    @SerializedName("name")        val name: String,
    @SerializedName("latitude")    val latitude: Double,
    @SerializedName("longitude")   val longitude: Double,
    @SerializedName("country")     val country: String?,
    @SerializedName("country_code") val countryCode: String?,
    @SerializedName("admin1")      val state: String?
)


data class WeatherResponse(
    @SerializedName("latitude")       val latitude: Double,
    @SerializedName("longitude")      val longitude: Double,
    @SerializedName("timezone")       val timezone: String,
    @SerializedName("current")        val current: CurrentWeather,
    @SerializedName("daily")          val daily: DailyWeather,
    @SerializedName("hourly")         val hourly: HourlyWeather
)

data class CurrentWeather(
    @SerializedName("time")                    val time: String,
    @SerializedName("temperature_2m")          val temperature: Double,
    @SerializedName("relative_humidity_2m")    val humidity: Int,
    @SerializedName("apparent_temperature")    val feelsLike: Double,
    @SerializedName("weather_code")            val weatherCode: Int,
    @SerializedName("wind_speed_10m")          val windSpeed: Double,
    @SerializedName("precipitation")           val precipitation: Double,
    @SerializedName("is_day")                  val isDay: Int           // 1 = day, 0 = night
)

data class DailyWeather(
    @SerializedName("time")                   val time: List<String>,
    @SerializedName("weather_code")           val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max")     val tempMax: List<Double>,
    @SerializedName("temperature_2m_min")     val tempMin: List<Double>,
    @SerializedName("precipitation_sum")      val precipitationSum: List<Double>
)

data class HourlyWeather(
    @SerializedName("time")               val time: List<String>,
    @SerializedName("temperature_2m")     val temperature: List<Double>,
    @SerializedName("weather_code")       val weatherCode: List<Int>
)