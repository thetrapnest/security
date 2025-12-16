package ru.thetrapnest.security.ui.theme

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
    primary = Aqua,
    onPrimary = Neutral900,
    secondary = Mint,
    onSecondary = Neutral900,
    tertiary = Amber,
    background = SurfaceDark,
    surface = Color(0xFF111827),
    onSurface = Neutral100,
    surfaceVariant = IndigoDark,
    onSurfaceVariant = Mist,
    primaryContainer = IndigoDark,
    onPrimaryContainer = Mist,
    secondaryContainer = Color(0xFF0B4A3F),
    onSecondaryContainer = Mist,
    tertiaryContainer = Color(0xFF5C3A00),
    onTertiaryContainer = Mist
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo,
    onPrimary = Neutral100,
    secondary = Mint,
    onSecondary = Neutral100,
    tertiary = Amber,
    background = SurfaceLight,
    surface = Neutral100,
    onSurface = Midnight,
    surfaceVariant = Mist,
    onSurfaceVariant = IndigoDark,
    primaryContainer = Aqua,
    onPrimaryContainer = Midnight,
    secondaryContainer = Color(0xFFD9FBE8),
    onSecondaryContainer = Midnight,
    tertiaryContainer = Color(0xFFFFF3E0),
    onTertiaryContainer = Midnight
)

@Composable
fun SecurityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
