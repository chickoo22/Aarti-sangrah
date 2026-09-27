package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SaffronPrimary = Color(0xFFFF6F00)
private val SaffronSecondary = Color(0xFFFFB300)
private val SaffronTertiary = Color(0xFFD84315)
private val WarmBackground = Color(0xFFFFF8E1)
private val DarkBackground = Color(0xFF121212)

private val LightColorScheme = lightColorScheme(
    primary = SaffronPrimary,
    secondary = SaffronSecondary,
    tertiary = SaffronTertiary,
    background = WarmBackground,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121)
)

private val DarkColorScheme = darkColorScheme(
    primary = SaffronSecondary,
    secondary = SaffronPrimary,
    tertiary = SaffronTertiary,
    background = DarkBackground,
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun MantramayaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
