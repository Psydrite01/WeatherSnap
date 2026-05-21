package com.example.weathersnap.data.repository

import com.example.weathersnap.data.local.ReportEntity
import kotlinx.coroutines.flow.Flow
import com.example.weathersnap.data.local.ReportDao
import javax.inject.Inject


interface ReportRepository {
    suspend fun saveReport(report: ReportEntity): Long
    fun getAllReports(): Flow<List<ReportEntity>>
    suspend fun deleteReport(id: Int)
}



class ReportRepositoryImpl @Inject constructor(
    private val reportDao: ReportDao
) : ReportRepository {

    override suspend fun saveReport(report: ReportEntity): Long =
        reportDao.insert(report)

    override fun getAllReports(): Flow<List<ReportEntity>> =
        reportDao.getAllReports()

    override suspend fun deleteReport(id: Int) =
        reportDao.deleteById(id)
}