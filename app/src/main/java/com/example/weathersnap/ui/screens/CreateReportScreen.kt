package com.example.weathersnap.ui.screens


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.weathersnap.navigation.CreateReportScreenRoute
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
import com.example.weathersnap.ui.theme.DarkBackground
import com.example.weathersnap.ui.theme.DarkSurface
import com.example.weathersnap.ui.theme.DarkSurfaceVariant
import com.example.weathersnap.ui.theme.HumidityText
import com.example.weathersnap.ui.theme.OliveGreen80
import com.example.weathersnap.ui.theme.OnDarkDisabled
import com.example.weathersnap.ui.theme.OnDarkPrimary
import com.example.weathersnap.ui.theme.OnDarkSecondary
import com.example.weathersnap.ui.theme.PressureText
import com.example.weathersnap.ui.theme.TopGradientEnd
import com.example.weathersnap.ui.theme.TopGradientStart
import com.example.weathersnap.ui.theme.WindText


private val ButtonFill = Color(0xFFCCDE6E)
private val PhotoGradientTopLeft     = Color(0xFF6B8C3A)
private val PhotoGradientBottomRight = Color(0xFF2A3D10)


@Composable
fun CreateReportScreen(
    onBack: () -> Unit = {}
) {
    var notes by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Header ────────────────────────────────────────────────────────
            CreateReportHeader(onBack = onBack)

            // ── Weather summary card ──────────────────────────────────────────
            WeatherSummaryCard(
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // ── Camera card (photo preview + capture button) ──────────────────
            CameraCard(
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // ── Field notes card ──────────────────────────────────────────────
            FieldNotesCard(
                notes    = notes,
                onNotesChange = { notes = it },
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // ── Save Report button ────────────────────────────────────────────
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(52.dp),
                shape  = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonFill,
                    contentColor   = Color(0xFF1A2710)
                )
            ) {
                Text(
                    text       = "Save Report",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp
                )
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CreateReportHeader(onBack: () -> Unit) {
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
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text       = "Create Report",
                style      = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color      = OnDarkPrimary
            )
            Text(
                text  = "Capture, compress, annotate",
                style = MaterialTheme.typography.bodySmall,
                color = OnDarkPrimary.copy(alpha = 0.78f)
            )
        }

        Button(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterEnd),
            shape  = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1A2A0A).copy(alpha = 0.7f),
                contentColor   = OnDarkPrimary
            ),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text       = "Back",
                fontWeight = FontWeight.SemiBold,
                fontSize   = 13.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Weather summary card  (mirrors WeatherDataCard minus the CTA)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun WeatherSummaryCard(modifier: Modifier = Modifier) {
    Surface(
        modifier       = modifier.fillMaxWidth(),
        shape          = RoundedCornerShape(14.dp),
        color          = DarkSurface,
        tonalElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // City + temperature
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

                Text(
                    text       = "00°C",
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color      = OnDarkPrimary
                )
            }

            Spacer(Modifier.height(14.dp))

            // Stat chips
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatChipSmall("Humidity",  "00%",       HumidityText, Modifier.weight(1f))
                StatChipSmall("Wind",      "0.00 m/s",  WindText,     Modifier.weight(1f))
                StatChipSmall("Pressure",  "000",       PressureText, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatChipSmall(
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

// ─────────────────────────────────────────────────────────────────────────────
//  Camera card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CameraCard(modifier: Modifier = Modifier) {
    Surface(
        modifier       = modifier.fillMaxWidth(),
        shape          = RoundedCornerShape(14.dp),
        color          = DarkSurface,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Photo preview area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                PhotoGradientTopLeft,
                                PhotoGradientBottomRight
                            ),
                            start = Offset(0f, 0f),
                            end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = "Photo preview",
                    color = OnDarkPrimary.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Capture Photo button
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape  = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonFill,
                    contentColor   = Color(0xFF1A2710)
                )
            ) {
                Text(
                    text       = "Capture Photo",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Field notes card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun FieldNotesCard(
    notes        : String,
    onNotesChange: (String) -> Unit,
    modifier     : Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text       = "Field Notes",
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color      = OnDarkPrimary
        )

        OutlinedTextField(
            value         = notes,
            onValueChange = onNotesChange,
            modifier      = Modifier
                .fillMaxWidth()
                .height(140.dp),
            placeholder   = {
                Text(
                    text  = "Notes",
                    color = OnDarkDisabled
                )
            },
            shape  = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor        = OnDarkPrimary,
                unfocusedTextColor      = OnDarkPrimary,
                focusedBorderColor      = OliveGreen80,
                unfocusedBorderColor    = OnDarkDisabled,
                cursorColor             = OliveGreen80,
                focusedContainerColor   = DarkSurface,
                unfocusedContainerColor = DarkSurface,
            )
        )
    }
}