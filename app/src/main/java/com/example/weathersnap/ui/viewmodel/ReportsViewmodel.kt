package com.example.weathersnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersnap.data.local.ReportEntity
import com.example.weathersnap.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted


@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val reportRepository: ReportRepository
) : ViewModel() {

    val reports: StateFlow<List<ReportEntity>> =
        reportRepository.getAllReports()
            .stateIn(
                scope         = viewModelScope,
                started       = SharingStarted.WhileSubscribed(5_000),
                initialValue  = emptyList()
            )
}