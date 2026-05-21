package com.example.weathersnap.ui.screens


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.weathersnap.navigation.CreateReportScreenRoute
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.weathersnap.domain.WeatherInfo
import com.example.weathersnap.ui.theme.DarkBackground
import com.example.weathersnap.ui.theme.DarkSurface
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
import com.example.weathersnap.ui.viewmodel.SaveReportState
import com.example.weathersnap.ui.viewmodel.WeatherViewModel
import com.example.weathersnap.util.CompressedImageResult
import com.example.weathersnap.util.ImageUtils
import kotlin.math.roundToInt


private val ButtonFill_lighttype = Color(0xFFCCDE6E)
private val ButtonFill_darktype = Color(0xFF2d3400)
private val PhotoGradientTopLeft     = Color(0xFF6B8C3A)
private val PhotoGradientBottomRight = Color(0xFF2A3D10)

private val HumidityBackground = Color(0xff353e35)
private val WindBackground = Color(0xff353c3c)
private val FeelsLikeBackground = Color(0xff403a2a)

@Composable
fun CreateReportScreen(
    onBack: () -> Unit,
    onOpenCamera: () -> Unit,
    onReportSaved: () -> Unit,
    viewModel: WeatherViewModel
) {
    var notes by remember { mutableStateOf("") }
    val info by viewModel.selectedWeather.collectAsState()
    val weatherinfo = info

    val imageResult by viewModel.capturedImage.collectAsState()

    val saveState    by viewModel.saveReportState.collectAsState()
    val savedNotes by viewModel.savedNotes.collectAsState()

    LaunchedEffect(savedNotes) {
        if (notes==""){
            notes = savedNotes
        }
    }

    // Navigate away when save succeeds
    LaunchedEffect(saveState) {
        if (saveState is SaveReportState.Saved) {
            viewModel.resetSaveState()
            viewModel.setCapturedImage(null)   // clear photo for next report
            onReportSaved()
        }
    }

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
            // ── Header ────────────────────────────────────────────────────────
            CreateReportHeader(onBack = onBack)

            // ── Weather summary card ──────────────────────────────────────────
            WeatherSummaryCard(
                modifier = Modifier.padding(horizontal = 12.dp),
                info = weatherinfo
            )

            // ── Camera card (photo preview + capture button) ──────────────────
            CameraCard(
                modifier      = Modifier.padding(horizontal = 12.dp),
                imageResult   = imageResult,
                onOpenCamera  = onOpenCamera
            )

            // ── Field notes card ──────────────────────────────────────────────
            FieldNotesCard(
                notes    = notes,
                onNotesChange = {
                    notes = it
                    viewModel.setSavedNotes(it)
                                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            // ── Save Report button ────────────────────────────────────────────
            val isSaving = saveState is SaveReportState.Saving

            Button(
                onClick  = { if (!isSaving) viewModel.saveReport(notes) },
                enabled  = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(52.dp),
                shape  = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor         = ButtonFill_lighttype,
                    contentColor           = Color(0xFF1A2710),
                    disabledContainerColor = ButtonFill_lighttype.copy(alpha = 0.6f)
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(20.dp),
                        color       = Color(0xFF1A2710),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text       = "Save Report",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 15.sp
                    )
                }
            }

            // Show error snackbar if save fails
            if (saveState is SaveReportState.Error) {
                Text(
                    text     = (saveState as SaveReportState.Error).message,
                    color    = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(Modifier.height(120.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CreateReportHeader(onBack: () -> Unit) {
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
                            text       = "Create Report",
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text  = "Capture, compress, annotate",
                            fontSize   = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = {
                            onBack()
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
                            text       = "Back",
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
//  Weather summary card  (mirrors WeatherDataCard minus the CTA)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun WeatherSummaryCard(
    info: WeatherInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
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
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Camera card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun CameraCard(
    imageResult : CompressedImageResult?,
    onOpenCamera: () -> Unit,
    modifier    : Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Photo preview / placeholder ───────────────────────────────
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
                if (imageResult != null) {
                    // Show captured & compressed image
                    AsyncImage(
                        model             = imageResult.uri,
                        contentDescription = "Captured photo",
                        contentScale      = ContentScale.Crop,
                        modifier          = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text  = "Photo preview",
                        color = OnDarkPrimary.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // ── Size info row (shown only after capture) ──────────────────
            if (imageResult != null) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SizeChip(
                        label    = "Original",
                        value    = ImageUtils.formatBytes(imageResult.originalSizeBytes),
                        modifier = Modifier.weight(1f)
                    )
                    SizeChip(
                        label    = "Compressed",
                        value    = ImageUtils.formatBytes(imageResult.compressedSizeBytes),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ── Capture button ────────────────────────────────────────────
            Button(
                onClick  = onOpenCamera,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape  = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonFill_lighttype,
                    contentColor   = Color(0xFF1A2710)
                )
            ) {
                Text(
                    text       = if (imageResult != null) "Retake Photo" else "Capture Photo",
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 14.sp
                )
            }
        }
    }
}

// ── Small chip to display file size ──────────────────────────────────────────
@Composable
private fun SizeChip(
    label   : String,
    value   : String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text     = label,
            fontSize = 10.sp,
            color    = OnDarkSecondary
        )
        Text(
            text       = value,
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color      = OnDarkPrimary
        )
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
    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Spacer(Modifier.height(6.dp))
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
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                ),
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
                    focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            )
        }
        Spacer(Modifier.height(6.dp))
    }
}