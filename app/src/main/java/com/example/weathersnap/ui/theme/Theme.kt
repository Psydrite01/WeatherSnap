package com.example.weathersnap.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary          = OliveGreen80,
    onPrimary        = Color(0xFF1A2710),
    primaryContainer = OliveGreen40,
    onPrimaryContainer = OliveGreen80,

    secondary          = OliveGreenGrey80,
    onSecondary        = Color(0xFF243317),
    secondaryContainer = OliveGreenGrey40,
    onSecondaryContainer = OliveGreenGrey80,

    tertiary          = MossGreen80,
    onTertiary        = Color(0xFF2E4010),
    tertiaryContainer = MossGreen40,
    onTertiaryContainer = MossGreen80,

    background  = Color(0xff22250b),
    onBackground = OnDarkPrimary,

    surface         = Color(0xff13140c),
    onSurface       = Color(0xff2e3501),
    surfaceVariant  = Color(0xff35352d),
    onSurfaceVariant = OnDarkSecondary,

    outline = OnDarkDisabled
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun WeatherSnapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }

    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}