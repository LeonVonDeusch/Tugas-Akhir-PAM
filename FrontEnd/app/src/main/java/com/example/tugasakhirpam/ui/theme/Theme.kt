package com.example.tugasakhirpam.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkBlueAccent,
    onPrimary = DarkBackground,
    secondary = DarkNavyPrimary,
    onSecondary = DarkBackground,
    tertiary = SkyAccent,
    background = DarkBackground,
    onBackground = DarkNavyPrimary,
    surface = DarkSurface,
    onSurface = DarkNavyPrimary,
    surfaceVariant = DarkSurfaceVariant,
    outline = DarkBorder,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = BlueAccent,
    onPrimary = SlateCard,
    secondary = NavyPrimary,
    onSecondary = SlateCard,
    tertiary = SkyAccent,
    background = SlateSurface,
    onBackground = SlateText,
    surface = SlateCard,
    onSurface = SlateText,
    surfaceVariant = SlateSurface,
    outline = SlateBorder,
    error = ErrorRed
)

@Composable
fun TugasAkhirPAMTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
