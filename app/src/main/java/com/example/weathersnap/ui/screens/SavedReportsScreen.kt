package com.example.weathersnap.ui.screens

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.weathersnap.data.local.ReportEntity
import com.example.weathersnap.ui.theme.*
import com.example.weathersnap.ui.viewmodel.ReportsViewModel
import com.example.weathersnap.util.ImageUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

private val ButtonFill_lighttype = Color(0xFFCCDE6E)
private val ButtonFill_darktype = Color(0xFF05372e)
private val ButtonFill_greytype = Color(0xFF424338)

private val CompressedBackground = Color(0xff353e35)
private val OriginalBackground = Color(0xff403a2a)


@Composable
fun SavedReportsScreen(
    onBack       : () -> Unit,
    viewModel    : ReportsViewModel = hiltViewModel()
) {
    val reports by viewModel.reports.collectAsState()

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
        Column(modifier = Modifier.fillMaxSize()) {

            SavedReportsHeader(
                reportCount = reports.size,
                onBack      = onBack
            )

            Spacer(Modifier.height(12.dp))

            if (reports.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text     = "No reports yet",
                            color    = OnDarkSecondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text     = "Create a report to see it here",
                            color    = OnDarkDisabled,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier            = Modifier.fillMaxSize(),
                    contentPadding      = PaddingValues(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reports, key = { it.id }) { report ->
                        ReportCard(report = report)
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Header  (unchanged layout, same as before)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SavedReportsHeader(
    reportCount: Int,
    onBack: () -> Unit) {
    Spacer(Modifier.height(42.dp))
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
                            Color(0xffa2d0c3),
                            Color(0xffc1cd7e)
                        )
                    )
                )
            ) {
                Row() {
                    Column(modifier = Modifier
                        .padding(16.dp)) {
                        Text(
                            text       = "Saved Reports",
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text  = "$reportCount report${if (reportCount != 1) "s" else ""} stored locally",
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
//  Report card  — now takes ReportEntity directly
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ReportCard(report: ReportEntity) {
    val formattedDate = remember(report.savedAt) {
        SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            .format(Date(report.savedAt))
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        color    = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column {

            // ── Photo thumbnail ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .height(180.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (report.photoUriString != null) {
                    AsyncImage(
                        model              = Uri.parse(report.photoUriString),
                        contentDescription = "Report photo",
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text     = "No photo",
                        color    = OnDarkDisabled,
                        fontSize = 13.sp
                    )
                }
            }

            // ── Info section ──────────────────────────────────────────────────
            Column(modifier = Modifier.padding(14.dp)) {

                // City + temperature badge
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text       = report.cityName,
                            style      = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color      = OnDarkPrimary
                        )
                        Text(
                            text  = report.condition,
                            style = MaterialTheme.typography.bodySmall,
                            color = OnDarkSecondary
                        )
                        Text(
                            text  = formattedDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = OnDarkDisabled
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(OliveGreen40)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text       = "${report.temperature.roundToInt()}°C",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color      = ButtonFill_lighttype
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Size chips — only if a photo was attached
                if (report.photoUriString != null) {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SizeChip(
                            label      = "Original",
                            labelColor = OriginalBackground,
                            value      = ImageUtils.formatBytes(report.originalSizeBytes),
                            valueColor = PressureText,
                            modifier   = Modifier.weight(1f)
                        )
                        SizeChip(
                            label      = "Compressed",
                            labelColor = CompressedBackground,
                            value      = ImageUtils.formatBytes(report.compressedSizeBytes),
                            valueColor = HumidityText,
                            modifier   = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                }

                // Notes chip — only if non-empty
                if (report.notes.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ButtonFill_greytype)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text  = report.notes,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Size chip
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SizeChip(
    label      : String,
    labelColor : Color,
    value      : String,
    valueColor : Color,
    modifier   : Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(labelColor)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column {
            Text(text = label, fontSize = 11.sp, color = OnDarkSecondary)
            Spacer(Modifier.height(2.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
        }
    }
}