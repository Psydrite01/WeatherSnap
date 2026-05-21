package com.example.weathersnap.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CachedCityEntity::class, ReportEntity::class],
    version = 3,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun cityDao(): CityDao
    abstract fun reportDao(): ReportDao
}