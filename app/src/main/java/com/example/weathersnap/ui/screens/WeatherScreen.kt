package com.example.weathersnap.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.weathersnap.domain.CityResult
import com.example.weathersnap.domain.WeatherInfo
import com.example.weathersnap.ui.theme.DarkSurfaceVariant
import com.example.weathersnap.ui.theme.HumidityText
import com.example.weathersnap.ui.theme.OliveGreen40
import com.example.weathersnap.ui.theme.OliveGreen80
import com.example.weathersnap.ui.theme.OnDarkDisabled
import com.example.weathersnap.ui.theme.OnDarkPrimary
import com.example.weathersnap.ui.theme.OnDarkSecondary
import com.example.weathersnap.ui.theme.PressureText
import com.example.weathersnap.ui.theme.WindText
import com.example.weathersnap.ui.viewmodel.SearchUiState
import com.example.weathersnap.ui.viewmodel.WeatherUiState
import com.example.weathersnap.ui.viewmodel.WeatherViewModel
import kotlin.math.roundToInt

private val ButtonFill_lighttype = Color(0xFFCCDE6E)
private val ButtonFill_darktype = Color(0xFF2d3400)

private val HumidityBackground = Color(0xff353e35)
private val WindBackground = Color(0xff353c3c)
private val FeelsLikeBackground = Color(0xff403a2a)


@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    navigateToCreateReport: () -> Unit,
    navigateToSavedReports: () -> Unit
) {
    val weatherState by viewModel.weatherState.collectAsState()
    val searchState  by viewModel.searchState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(colors = listOf(
                    MaterialTheme.colorScheme.tertiary,
                    MaterialTheme.colorScheme.background
                ))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Header ────────────────────────────────────────────────────
            WeatherSnapHeader(
                navigateToSavedReports
            )

            // ── City search card ──────────────────────────────────────────
            CitySearchCard(
                modifier     = Modifier.padding(horizontal = 12.dp),
                searchState  = searchState,
                onQueryChange    = viewModel::onSearchQueryChange,
                onSearchClick    = {viewModel.retry()},
                onCitySelected   = viewModel::onCitySelected,
                onDismissDropdown = viewModel::dismissDropdown,
                onClearSearch    = viewModel::clearSearch
            )

            // ── Weather data card ─────────────────────────────────────────
            WeatherDataCard(
                modifier       = Modifier.padding(horizontal = 12.dp),
                weatherState   = weatherState,
                navigateToCreateReport = navigateToCreateReport,
                viewModel
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Header   (gradient green band)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun WeatherSnapHeader(
    navigateToSavedReports: () -> Unit
) {
    Spacer(Modifier.height(30.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp, start = 12.dp, end = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Box(
                modifier = Modifier.background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xffc1cd7e),
                            Color(0xffa2d0c3)
                        )
                    )
                )
            ) {
            Row() {
                Column(modifier = Modifier
                    .padding(16.dp)) {
                    Text(
                        text       = "WeatherSnap",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text  = "Live weather reports with camera evidence",
                        fontSize   = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.weight(1f))
                Button(
                    onClick = {
                        navigateToSavedReports()
                    },
                    modifier = Modifier
                        .padding(top = 16.dp, start = 8.dp, end = 16.dp, bottom = 16.dp),
                    shape  = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonFill_darktype,
                        contentColor   = MaterialTheme.colorScheme.onSurface
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text       = "Reports",
                        fontWeight = FontWeight.SemiBold,
                        color = ButtonFill_lighttype,
                        fontSize   = 13.sp
                    )
                }
            }
            }
        }

    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  City search card  — now driven by SearchUiState
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CitySearchCard(
    modifier          : Modifier = Modifier,
    searchState       : SearchUiState,
    onQueryChange     : (String) -> Unit,
    onSearchClick     : () -> Unit,
    onCitySelected    : (CityResult) -> Unit,
    onDismissDropdown : () -> Unit,
    onClearSearch     : () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Surface(
        modifier       = modifier.fillMaxWidth(),
        shape          = RoundedCornerShape(12.dp),
        color          = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── Input row ─────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value         = searchState.query,
                    onValueChange = onQueryChange,
                    modifier      = Modifier.weight(1f),
                    label         = { Text("City", color = OnDarkSecondary, fontSize = 12.sp) },
                    singleLine    = true,
                    shape         = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    ),
                    trailingIcon  = {
                        // Show spinner while fetching suggestions
                        if (searchState.isSearching) {
                            CircularProgressIndicator(
                                modifier    = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color       = OliveGreen80
                            )
                        } else if (searchState.query.isNotEmpty()) {
                            IconButton(onClick = {
                                onClearSearch()
                                focusManager.clearFocus()
                            }) {
                                Icon(
                                    imageVector        = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint               = OnDarkSecondary
                                )
                            }
                        }
                    },
                    colors    = OutlinedTextFieldDefaults.colors(
                        focusedTextColor        = OnDarkPrimary,
                        unfocusedTextColor      = OnDarkPrimary,
                        focusedBorderColor      = OliveGreen80,
                        unfocusedBorderColor    = OnDarkDisabled,
                        cursorColor             = OliveGreen80,
                        focusedLabelColor       = OliveGreen80,
                        unfocusedLabelColor     = OnDarkSecondary,
                        focusedContainerColor   = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 15.sp)
                )

                Button(
                    onClick = {
                        onSearchClick()
                        focusManager.clearFocus()
                    },
                    shape  = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonFill_lighttype,
                        contentColor   = Color(0xFF1A2710)
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Text(
                        text       = "Search",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text  = "Enter more than 2 letters to start city suggestions.",
                style = MaterialTheme.typography.labelSmall,
                color = OnDarkDisabled
            )

            // ── Suggestions dropdown ──────────────────────────────────────
            AnimatedVisibility(
                visible = searchState.isDropdownVisible && searchState.suggestions.isNotEmpty(),
                enter   = fadeIn() + expandVertically(),
                exit    = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurfaceVariant)
                ) {
                    searchState.suggestions.forEachIndexed { index, city ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onCitySelected(city)
                                    focusManager.clearFocus()
                                }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint               = OliveGreen80,
                                modifier           = Modifier.size(16.dp)
                            )
                            Column {
                                Text(
                                    text       = city.name,
                                    fontSize   = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color      = OnDarkPrimary
                                )
                                val subtitle = buildString {
                                    if (city.state.isNotBlank()) append("${city.state}, ")
                                    append(city.country)
                                }
                                if (subtitle.isNotBlank()) {
                                    Text(
                                        text      = subtitle,
                                        fontSize  = 12.sp,
                                        color     = OnDarkSecondary,
                                        maxLines  = 1,
                                        overflow  = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        if (index < searchState.suggestions.lastIndex) {
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color     = OnDarkDisabled.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Weather data card — now driven by WeatherUiState
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun WeatherDataCard(
    modifier               : Modifier = Modifier,
    weatherState           : WeatherUiState,
    navigateToCreateReport : () -> Unit,
    viewModel: WeatherViewModel
) {
    Surface(
        modifier       = modifier.fillMaxWidth(),
        shape          = RoundedCornerShape(14.dp),
        color          = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        // Animate between states cleanly
        AnimatedContent(
            targetState    = weatherState,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
            label          = "weather_card_content"
        ) { state ->
            when (state) {

                // ── Idle ──────────────────────────────────────────────────
                is WeatherUiState.Idle -> {
                    WeatherCardPlaceholder(navigateToCreateReport = navigateToCreateReport)
                }

                // ── Loading ───────────────────────────────────────────────
                is WeatherUiState.Loading -> {
                    Box(
                        modifier        = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(color = OliveGreen80)
                            Text(
                                text  = "Fetching weather…",
                                style = MaterialTheme.typography.bodySmall,
                                color = OnDarkSecondary
                            )
                        }
                    }
                }

                // ── Success ───────────────────────────────────────────────
                is WeatherUiState.Success -> {
                    WeatherCardContent(
                        info                   = state.weatherInfo,
                        navigateToCreateReport = navigateToCreateReport,
                        viewModel,
                    )
                }

                // ── Error ─────────────────────────────────────────────────
                is WeatherUiState.Error -> {
                    Box(
                        modifier        = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "⚠️", fontSize = 32.sp)
                            Text(
                                text       = state.message,
                                style      = MaterialTheme.typography.bodySmall,
                                color      = OnDarkSecondary,
                                textAlign  = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Placeholder (Idle state) — mirrors your original hard-coded layout
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun WeatherCardPlaceholder(navigateToCreateReport: () -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text  = "Search a city above",
                style = MaterialTheme.typography.bodyMedium,
                color = OnDarkSecondary
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Populated content (Success state)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun WeatherCardContent(
    info                   : WeatherInfo,
    navigateToCreateReport : () -> Unit,
    viewModel: WeatherViewModel
) {
    Column(modifier = Modifier.padding(16.dp)) {

        // ── City + temperature ─────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = info.cityName,
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color      = OnDarkPrimary,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment      = Alignment.CenterVertically,
                    horizontalArrangement  = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = info.current.iconRes.emoji, fontSize = 14.sp)
                    Text(
                        text  = info.current.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnDarkSecondary
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            // Temperature badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(OliveGreen40)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text       = "${info.current.temperature.roundToInt()}°C",
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = ButtonFill_lighttype
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Stat chips ─────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatChip(
                label      = "Humidity",
                labelColor = HumidityBackground,
                value      = "${info.current.humidity}%",
                valueColor = HumidityText,
                modifier   = Modifier.weight(1f)
            )
            StatChip(
                label      = "Wind",
                labelColor = WindBackground,
                value      = "${info.current.windSpeed} m/s",
                valueColor = WindText,
                modifier   = Modifier.weight(1f)
            )
            StatChip(
                label      = "Feels like",
                labelColor = FeelsLikeBackground,
                value      = "${info.current.feelsLike.roundToInt()}°C",
                valueColor = PressureText,
                modifier   = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        // ── Report readiness ────────────────────────────────────────────
        Card(
            modifier = Modifier,
            colors = CardDefaults.cardColors(
                containerColor = Color(0xff424338)
            )
        ) {
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text  = "Report readiness",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnDarkSecondary
                )
                Text(
                    text       = "Camera and Room DB enabled",
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = OnDarkPrimary
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Create Report button ────────────────────────────────────────
        Button(
            onClick  = {
                viewModel.setSelectedWeather(info)
                navigateToCreateReport()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape    = RoundedCornerShape(14.dp),
            colors   = ButtonDefaults.buttonColors(
                containerColor = ButtonFill_lighttype,
                contentColor   = Color(0xFF1A2710)
            )
        ) {
            Text(
                text       = "Create Report",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 15.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Stat chip
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun StatChip(
    label      : String,
    labelColor:Color,
    value      : String,
    valueColor : Color,
    modifier   : Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(labelColor)
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Column {
            Text(
                text     = label,
                fontSize = 11.sp,
                color    = OnDarkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text       = value,
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color      = valueColor,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
        }
    }
}