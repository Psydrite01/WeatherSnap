package com.example.weathersnap.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersnap.data.local.ReportEntity
import com.example.weathersnap.data.repository.ReportRepository
import com.example.weathersnap.data.repository.WeatherRepository
import com.example.weathersnap.domain.CityResult
import com.example.weathersnap.domain.CurrentConditions
import com.example.weathersnap.domain.WeatherIcon
import com.example.weathersnap.domain.WeatherInfo
import com.example.weathersnap.util.CompressedImageResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


// ── UI State ──────────────────────────────────

sealed interface WeatherUiState {
    data object Idle : WeatherUiState
    data object Loading : WeatherUiState
    data class Success(val weatherInfo: WeatherInfo) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

sealed interface SaveReportState {
    data object Idle    : SaveReportState
    data object Saving  : SaveReportState
    data object Saved   : SaveReportState
    data class  Error(val message: String) : SaveReportState
}

data class SearchUiState(
    val query: String = "",
    val suggestions: List<CityResult> = emptyList(),
    val isSearching: Boolean = false,
    val isDropdownVisible: Boolean = false
)



// ── ViewModel ─────────────────────────────────

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val reportRepository: ReportRepository
) : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    private val _searchState = MutableStateFlow(SearchUiState())
    val searchState: StateFlow<SearchUiState> = _searchState.asStateFlow()

    private val _capturedImage = MutableStateFlow<CompressedImageResult?>(null)
    val capturedImage: StateFlow<CompressedImageResult?> = _capturedImage.asStateFlow()

    private val _saveReportState = MutableStateFlow<SaveReportState>(SaveReportState.Idle)
    val saveReportState: StateFlow<SaveReportState> = _saveReportState.asStateFlow()

    private val _savedNotes = MutableStateFlow<String>("")
    val savedNotes: StateFlow<String> = _savedNotes.asStateFlow()


    private val _selectedWeather = MutableStateFlow(
        WeatherInfo(
            cityName = "",
            latitude = 0.0,
            longitude = 0.0,
            timezone = "",
            current = CurrentConditions(0.0, 0.0, 0, 0.0, 0.0, 0, true, "", WeatherIcon.CLEAR_DAY),
            dailyForecasts = emptyList(),
            hourlyForecasts = emptyList()
        ))
    val selectedWeather: StateFlow<WeatherInfo> = _selectedWeather.asStateFlow()

    // Internal flow to debounce search input
    private val _searchQuery = MutableStateFlow("")

    init {
        observeSearchQuery()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(400L)
                .distinctUntilChanged()
                .filter { it.length >= 2 }
                .collect { query -> fetchCitySuggestions(query) }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchState.update { it.copy(query = query) }
        _searchQuery.value = query

        if (query.length < 2) {
            _searchState.update {
                it.copy(suggestions = emptyList(), isDropdownVisible = false)
            }
        }
    }

    private fun fetchCitySuggestions(query: String) {
        viewModelScope.launch {
            Log.d("WeatherViewModel", "fetchCitySuggestions: query=$query")
            _searchState.update { it.copy(isSearching = true) }

            repository.searchCity(query).fold(
                onSuccess = { cities ->
                    Log.d("WeatherViewModel", "fetchCitySuggestions: Success, ${cities.size} found")
                    _searchState.update {
                        it.copy(
                            suggestions = cities,
                            isSearching = false,
                            isDropdownVisible = cities.isNotEmpty()
                        )
                    }
                },
                onFailure = { error ->
                    Log.e("WeatherViewModel", "fetchCitySuggestions: Error", error)
                    _searchState.update {
                        it.copy(
                            suggestions = emptyList(),
                            isSearching = false,
                            isDropdownVisible = false
                        )
                    }
                }
            )
        }
    }

    fun onCitySelected(city: CityResult) {
        // Clear search UI
        _searchState.update {
            it.copy(
                query = "${city.name}, ${city.country}",
                suggestions = emptyList(),
                isDropdownVisible = false
            )
        }
        loadWeather(city)
    }

    private fun loadWeather(city: CityResult) {
        viewModelScope.launch {
            Log.d("WeatherViewModel", "loadWeather: ${city.name}")
            _weatherState.value = WeatherUiState.Loading

            repository.getWeather(
                latitude = city.latitude,
                longitude = city.longitude,
                cityName = "${city.name}, ${city.country}"
            ).fold(
                onSuccess = { info ->
                    Log.d("WeatherViewModel", "loadWeather: Success")
                    _weatherState.value = WeatherUiState.Success(info)
                },
                onFailure = { error ->
                    Log.e("WeatherViewModel", "loadWeather: Error", error)
                    _weatherState.value = WeatherUiState.Error(
                        error.localizedMessage ?: "Failed to fetch weather"
                    )
                }
            )
        }
    }

    fun dismissDropdown() {
        _searchState.update { it.copy(isDropdownVisible = false) }
    }

    fun clearSearch() {
        _searchState.value = SearchUiState()
        _searchQuery.value = ""
    }

    fun retry() {
        val currentQuery = _searchState.value.query
        if (currentQuery.isNotBlank()) {
            fetchCitySuggestions(currentQuery)
        }
    }

    fun setSelectedWeather(info: WeatherInfo){
        _selectedWeather.value = info
    }

    fun setCapturedImage(result: CompressedImageResult?) {
        _capturedImage.value = result
    }

    fun saveReport(notes: String) {
        val weather = _selectedWeather.value
        val image   = _capturedImage.value
        Log.d("WeatherViewModel", "saveReport: city=${weather.cityName}")

        viewModelScope.launch {
            _saveReportState.value = SaveReportState.Saving
            try {
                val entity = ReportEntity(
                    cityName = weather.cityName,
                    condition = weather.current.description,
                    temperature = weather.current.temperature,
                    humidity = weather.current.humidity,
                    windSpeed = weather.current.windSpeed,
                    feelsLike = weather.current.feelsLike,
                    photoUriString = image?.uri?.toString(),
                    originalSizeBytes = image?.originalSizeBytes ?: 0L,
                    compressedSizeBytes = image?.compressedSizeBytes ?: 0L,
                    notes = notes,
                    savedAt = System.currentTimeMillis()
                )
                reportRepository.saveReport(entity)
                Log.d("WeatherViewModel", "saveReport: Success")
                _saveReportState.value = SaveReportState.Saved
                setSavedNotes()
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "saveReport: Error", e)
                _saveReportState.value = SaveReportState.Error(e.localizedMessage ?: "Save failed")
            }
        }
    }

    fun setSavedNotes(value: String=""){
        _savedNotes.value = value
    }

    fun resetSaveState() { _saveReportState.value = SaveReportState.Idle }

}