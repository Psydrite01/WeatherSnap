package com.example.weathersnap.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(report: ReportEntity): Long

    @Query("SELECT * FROM saved_reports ORDER BY savedAt DESC")
    fun getAllReports(): Flow<List<ReportEntity>>

    @Query("DELETE FROM saved_reports WHERE id = :id")
    suspend fun deleteById(id: Int)
}