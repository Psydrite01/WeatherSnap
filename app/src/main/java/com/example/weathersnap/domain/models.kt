package com.example.weathersnap.domain

import com.example.weathersnap.data.GeocodingResult
import com.example.weathersnap.data.WeatherResponse
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class CityResult(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String,
    val state: String
)

data class WeatherInfo(
    val cityName: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: CurrentConditions,
    val dailyForecasts: List<DailyForecast>,
    val hourlyForecasts: List<HourlyForecast>
)

data class CurrentConditions(
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val precipitation: Double,
    val weatherCode: Int,
    val isDay: Boolean,
    val description: String,
    val iconRes: WeatherIcon
)

data class DailyForecast(
    val date: String,         // formatted e.g. "Mon, 20 May"
    val rawDate: String,      // "2025-05-20"
    val weatherCode: Int,
    val tempMax: Double,
    val tempMin: Double,
    val precipitationSum: Double,
    val description: String,
    val iconRes: WeatherIcon
)

data class HourlyForecast(
    val time: String,         // formatted "14:00"
    val temperature: Double,
    val weatherCode: Int,
    val iconRes: WeatherIcon
)

// Sealed class to represent weather icons as emoji + label
enum class WeatherIcon(val emoji: String, val label: String) {
    CLEAR_DAY("☀️", "Clear"),
    CLEAR_NIGHT("🌙", "Clear Night"),
    PARTLY_CLOUDY_DAY("⛅", "Partly Cloudy"),
    PARTLY_CLOUDY_NIGHT("☁️", "Cloudy Night"),
    OVERCAST("☁️", "Overcast"),
    FOG("🌫️", "Foggy"),
    DRIZZLE("🌦️", "Drizzle"),
    RAIN("🌧️", "Rain"),
    SNOW("❄️", "Snow"),
    THUNDERSTORM("⛈️", "Thunderstorm"),
    UNKNOWN("🌡️", "Unknown")
}

fun GeocodingResult.toDomain(): CityResult = CityResult(
    id = id,
    name = name,
    latitude = latitude,
    longitude = longitude,
    country = country ?: "",
    state = state ?: ""
)

fun WeatherResponse.toDomain(cityName: String): WeatherInfo {
    val current = CurrentConditions(
        temperature = current.temperature,
        feelsLike = current.feelsLike,
        humidity = current.humidity,
        windSpeed = current.windSpeed,
        precipitation = current.precipitation,
        weatherCode = current.weatherCode,
        isDay = current.isDay == 1,
        description = weatherCodeToDescription(current.weatherCode),
        iconRes = weatherCodeToIcon(current.weatherCode, current.isDay == 1)
    )

    val dailyForecasts = daily.time.indices.map { i ->
        val rawDate = daily.time[i]
        DailyForecast(
            rawDate = rawDate,
            date = formatDate(rawDate),
            weatherCode = daily.weatherCode[i],
            tempMax = daily.tempMax[i],
            tempMin = daily.tempMin[i],
            precipitationSum = daily.precipitationSum[i],
            description = weatherCodeToDescription(daily.weatherCode[i]),
            iconRes = weatherCodeToIcon(daily.weatherCode[i], true)
        )
    }

    // Show next 24 hours of hourly data
    val now = LocalTime.now()
    val hourlyForecasts = hourly.time.indices
        .map { i ->
            val timePart = hourly.time[i].substringAfter("T")
            HourlyForecast(
                time = timePart.substring(0, 5),  // "HH:mm"
                temperature = hourly.temperature[i],
                weatherCode = hourly.weatherCode[i],
                iconRes = weatherCodeToIcon(hourly.weatherCode[i], true)
            )
        }
        .take(24)

    return WeatherInfo(
        cityName = cityName,
        latitude = latitude,
        longitude = longitude,
        timezone = timezone,
        current = current,
        dailyForecasts = dailyForecasts,
        hourlyForecasts = hourlyForecasts
    )
}

private fun formatDate(raw: String): String {
    return try {
        val date = LocalDate.parse(raw)
        val formatter = DateTimeFormatter.ofPattern("EEE, d MMM", Locale.ENGLISH)
        date.format(formatter)
    } catch (e: Exception) { raw }
}

fun weatherCodeToDescription(code: Int): String = when (code) {
    0 -> "Clear Sky"
    1 -> "Mainly Clear"
    2 -> "Partly Cloudy"
    3 -> "Overcast"
    45, 48 -> "Foggy"
    51, 53, 55 -> "Drizzle"
    61, 63, 65 -> "Rain"
    71, 73, 75 -> "Snow"
    80, 81, 82 -> "Rain Showers"
    85, 86 -> "Snow Showers"
    95 -> "Thunderstorm"
    96, 99 -> "Thunderstorm with Hail"
    else -> "Unknown"
}

fun weatherCodeToIcon(code: Int, isDay: Boolean): WeatherIcon = when (code) {
    0, 1 -> if (isDay) WeatherIcon.CLEAR_DAY else WeatherIcon.CLEAR_NIGHT
    2 -> if (isDay) WeatherIcon.PARTLY_CLOUDY_DAY else WeatherIcon.PARTLY_CLOUDY_NIGHT
    3 -> WeatherIcon.OVERCAST
    45, 48 -> WeatherIcon.FOG
    51, 53, 55 -> WeatherIcon.DRIZZLE
    61, 63, 65, 80, 81, 82 -> WeatherIcon.RAIN
    71, 73, 75, 85, 86 -> WeatherIcon.SNOW
    95, 96, 99 -> WeatherIcon.THUNDERSTORM
    else -> WeatherIcon.UNKNOWN
}