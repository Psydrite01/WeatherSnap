package com.example.weathersnap.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersnap.data.repository.WeatherRepository
import com.example.weathersnap.domain.CityResult
import com.example.weathersnap.domain.WeatherInfo
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

data class SearchUiState(
    val query: String = "",
    val suggestions: List<CityResult> = emptyList(),
    val isSearching: Boolean = false,
    val isDropdownVisible: Boolean = false
)

// ── ViewModel ─────────────────────────────────

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    private val _searchState = MutableStateFlow(SearchUiState())
    val searchState: StateFlow<SearchUiState> = _searchState.asStateFlow()

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
            _searchState.update { it.copy(isSearching = true) }

            repository.searchCity(query).fold(
                onSuccess = { cities ->
                    _searchState.update {
                        it.copy(
                            suggestions = cities,
                            isSearching = false,
                            isDropdownVisible = cities.isNotEmpty()
                        )
                    }
                },
                onFailure = { error ->
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
            _weatherState.value = WeatherUiState.Loading

            repository.getWeather(
                latitude = city.latitude,
                longitude = city.longitude,
                cityName = "${city.name}, ${city.country}"
            ).fold(
                onSuccess = { info ->
                    _weatherState.value = WeatherUiState.Success(info)
                },
                onFailure = { error ->
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
}