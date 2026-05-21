package com.example.weathersnap.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CityDao {

    @Query("SELECT * FROM city_suggestions WHERE query = :query")
    suspend fun getByQuery(query: String): CachedCityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CachedCityEntity)
}