package com.example.weathersnap.ui.screens


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.weathersnap.navigation.CreateReportScreenRoute
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weathersnap.data.SavedReport
import com.example.weathersnap.ui.theme.DarkBackground
import com.example.weathersnap.ui.theme.DarkSurface
import com.example.weathersnap.ui.theme.DarkSurfaceVariant
import com.example.weathersnap.ui.theme.HumidityText
import com.example.weathersnap.ui.theme.OliveGreen40
import com.example.weathersnap.ui.theme.OnDarkDisabled
import com.example.weathersnap.ui.theme.OnDarkPrimary
import com.example.weathersnap.ui.theme.OnDarkSecondary
import com.example.weathersnap.ui.theme.PressureText
import com.example.weathersnap.ui.theme.TopGradientEnd
import com.example.weathersnap.ui.theme.TopGradientStart


private val ButtonFill = Color(0xFFCCDE6E)


@Composable
fun SavedReportsScreen(
    reports: List<SavedReport> = listOf(SavedReport()), // default one placeholder card
    onBack : () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ────────────────────────────────────────────────────────
            SavedReportsHeader(
                reportCount = reports.size,
                onBack      = onBack
            )

            Spacer(Modifier.height(12.dp))

            // ── Report cards list ─────────────────────────────────────────────
            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reports) { report ->
                    ReportCard(report = report)
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Header
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SavedReportsHeader(
    reportCount: Int,
    onBack     : () -> Unit
) {
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
                text       = "Saved Reports",
                style      = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color      = OnDarkPrimary
            )
            Text(
                text  = "$reportCount report${if (reportCount != 1) "s" else ""} stored locally",
                style = MaterialTheme.typography.bodySmall,
                color = OnDarkPrimary.copy(alpha = 0.78f)
            )
        }

        Button(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterEnd),
            shape  = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OliveGreen40,
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
//  Report card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun ReportCard(report: SavedReport) {
    Surface(
        modifier       = Modifier.fillMaxWidth(),
        shape          = RoundedCornerShape(14.dp),
        color          = DarkSurface,
        tonalElevation = 2.dp
    ) {
        Column {

            // ── Photo thumbnail (black box) ───────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                    .background(Color.Black)
            )

            // ── Info section ──────────────────────────────────────────────────
            Column(modifier = Modifier.padding(14.dp)) {

                // City + temperature badge
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.Top
                ) {
                    Column {
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
                            text  = report.dateTime,
                            style = MaterialTheme.typography.labelSmall,
                            color = OnDarkDisabled
                        )
                    }

                    // Temperature badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(OliveGreen40)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text       = report.temperature,
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color      = ButtonFill
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Size chips row
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SizeChip(
                        label      = "Original",
                        value      = report.originalSize,
                        valueColor = PressureText,
                        modifier   = Modifier.weight(1f)
                    )
                    SizeChip(
                        label      = "Compressed",
                        value      = report.compressedSize,
                        valueColor = HumidityText,
                        modifier   = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Tag chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text  = report.tag,
                        style = MaterialTheme.typography.labelMedium,
                        color = OnDarkSecondary
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Size chip   (Original / Compressed)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SizeChip(
    label      : String,
    value      : String,
    valueColor : Color,
    modifier   : Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceVariant)
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column {
            Text(
                text     = label,
                fontSize = 11.sp,
                color    = OnDarkSecondary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text       = value,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = valueColor
            )
        }
    }
}