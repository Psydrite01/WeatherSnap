package com.example.weathersnap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id              : Int    = 0,

    // Weather
    val cityName        : String,
    val condition       : String,
    val temperature     : Double,   // raw °C, formatted on display
    val humidity        : Int,
    val windSpeed       : Double,
    val feelsLike       : Double,

    // Photo
    val photoUriString  : String?,  // FileProvider URI as string; null if no photo taken
    val originalSizeBytes   : Long = 0L,
    val compressedSizeBytes : Long = 0L,

    // Notes
    val notes           : String,

    // Meta
    val savedAt         : Long = System.currentTimeMillis()   // epoch ms
)