package com.example.weathersnap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_suggestions")
data class CachedCityEntity(
    @PrimaryKey
    val query: String,              // the search term, e.g. "mum"
    val citiesJson: String,         // Gson-serialized List<CityResult>
    val cachedAt: Long              // System.currentTimeMillis()
)