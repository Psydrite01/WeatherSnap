package com.example.weathersnap.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathersnap.ui.theme.DarkSurfaceVariant
import com.example.weathersnap.ui.theme.HumidityText
import com.example.weathersnap.ui.theme.OliveGreen40
import com.example.weathersnap.ui.theme.OliveGreen80
import com.example.weathersnap.ui.theme.OnDarkDisabled
import com.example.weathersnap.ui.theme.OnDarkPrimary
import com.example.weathersnap.ui.theme.OnDarkSecondary
import com.example.weathersnap.ui.theme.PressureText
import com.example.weathersnap.ui.theme.TopGradientEnd
import com.example.weathersnap.ui.theme.TopGradientStart
import com.example.weathersnap.ui.theme.WindText


private val ButtonFill = Color(0xFFCCDE6E)

@Composable
fun WeatherScreen(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Header ────────────────────────────────────────────────────
            WeatherSnapHeader()

            // ── City search card ──────────────────────────────────────────
            CitySearchCard(
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // ── Weather data card ─────────────────────────────────────────
            WeatherDataCard(
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Header   (gradient green band)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun WeatherSnapHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(TopGradientStart, TopGradientEnd),
                    start  = Offset(0f, 0f),
                    end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            )
            .padding(horizontal = 20.dp, vertical = 22.dp)
    ) {
        // Title + subtitle
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text       = "WeatherSnap",
                style      = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color      = OnDarkPrimary
            )
            Text(
                text  = "Live weather reports with camera evidence",
                style = MaterialTheme.typography.bodySmall,
                color = OnDarkPrimary.copy(alpha = 0.78f)
            )
        }

        // Reports button
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier.align(Alignment.CenterEnd),
            shape  = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OliveGreen40,
                contentColor   = OnDarkPrimary
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text       = "Reports",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 13.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  City search card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CitySearchCard(modifier: Modifier = Modifier) {
    var cityText by remember { mutableStateOf("Placeholder City") }

    Surface(
        modifier      = modifier.fillMaxWidth(),
        shape         = RoundedCornerShape(12.dp),
        color         = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Label + text field row
            Row(
                modifier            = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment   = Alignment.CenterVertically
            ) {
                // Outlined text field (city name)
                OutlinedTextField(
                    value         = cityText,
                    onValueChange = { cityText = it },
                    modifier      = Modifier.weight(1f),
                    label         = { Text("City", color = OnDarkSecondary, fontSize = 12.sp) },
                    singleLine    = true,
                    shape         = RoundedCornerShape(8.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedTextColor       = OnDarkPrimary,
                        unfocusedTextColor     = OnDarkPrimary,
                        focusedBorderColor     = OliveGreen80,
                        unfocusedBorderColor   = OnDarkDisabled,
                        cursorColor            = OliveGreen80,
                        focusedLabelColor      = OliveGreen80,
                        unfocusedLabelColor    = OnDarkSecondary,
                        focusedContainerColor  = DarkSurfaceVariant,
                        unfocusedContainerColor = DarkSurfaceVariant,
                    ),
                    textStyle = LocalTextStyle.current.copy(fontSize = 15.sp)
                )

                // Search button
                Button(
                    onClick = { /* TODO */ },
                    shape  = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonFill,
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
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Weather data card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun WeatherDataCard(modifier: Modifier = Modifier) {
    Surface(
        modifier       = modifier.fillMaxWidth(),
        shape          = RoundedCornerShape(14.dp),
        color          = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // ── City + temperature ─────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Column {
                    Text(
                        text       = "Placeholder City",
                        style      = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color      = OnDarkPrimary
                    )
                    Text(
                        text  = "Partly cloudy",
                        style = MaterialTheme.typography.bodyMedium,
                        color = OnDarkSecondary
                    )
                }

                // Temperature badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(OliveGreen40)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text       = "00°C",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color      = ButtonFill
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Stat chips row ─────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatChip(
                    label = "Humidity",
                    value = "00%",
                    valueColor = HumidityText,
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    label = "Wind",
                    value = "0.00 m/s",
                    valueColor = WindText,
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    label = "Pressure",
                    value = "000",
                    valueColor = PressureText,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── Report readiness row ───────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
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

            Spacer(Modifier.height(16.dp))

            // ── Create Report button ───────────────────────────────────────
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonFill,
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
}

// ─────────────────────────────────────────────────────────────────────────────
//  Stat chip   (Humidity / Wind / Pressure)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun StatChip(
    label      : String,
    value      : String,
    valueColor : Color,
    modifier   : Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurfaceVariant)
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