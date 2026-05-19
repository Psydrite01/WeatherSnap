package com.example.weathersnap.data

data class SavedReport(
    val cityName      : String = "Placeholder City",
    val condition     : String = "Partly cloudy",
    val dateTime      : String = "00 Jan 2026, 00:00 pm",
    val temperature   : String = "00°C",
    val originalSize  : String = "000 KB",
    val compressedSize: String = "00 KB",
    val tag           : String = "placeholder_report"
)