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
    primary = ElectricBlue,
    onPrimary = Neutral900,
    secondary = Aqua,
    onSecondary = Neutral900,
    tertiary = Sun,
    background = SurfaceDark,
    surface = Color(0xFF0C1627),
    onSurface = Neutral100,
    surfaceVariant = Color(0xFF12233D),
    onSurfaceVariant = Neutral200,
    primaryContainer = Color(0xFF103257),
    onPrimaryContainer = Neutral100,
    secondaryContainer = Color(0xFF112B35),
    onSecondaryContainer = Neutral100,
    tertiaryContainer = Color(0xFF3D3111),
    onTertiaryContainer = Neutral100
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo,
    onPrimary = Neutral100,
    secondary = Aqua,
    onSecondary = Neutral900,
    tertiary = Amber,
    background = SurfaceLight,
    surface = Neutral100,
    onSurface = Midnight,
    surfaceVariant = Color(0xFFE9F0FF),
    onSurfaceVariant = Color(0xFF476079),
    primaryContainer = Color(0xFFDCE8FF),
    onPrimaryContainer = Midnight,
    secondaryContainer = Color(0xFFDFFBFF),
    onSecondaryContainer = Midnight,
    tertiaryContainer = Color(0xFFFFF1D7),
    onTertiaryContainer = Midnight
)

@Composable
fun SecurityTheme(
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
