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

private val LightColorScheme = lightColorScheme(
    primary = BhagwaPrimary,                  // Deep Saffron / Bhagwa (#FF9933)
    onPrimary = Color(0xFF261400),            // Deep brown for legibility on #FF9933
    primaryContainer = BhagwaContainer,       // Soft warm saffron tint (#FFE0B2)
    onPrimaryContainer = BhagwaOnContainer,   // High contrast deep brown text (#4E2600)
    secondary = BhagwaDeepSaffron,            // Rich Saffron accent (#E65100)
    onSecondary = Color.White,
    secondaryContainer = BhagwaContainer,
    onSecondaryContainer = BhagwaDark,
    tertiary = BhagwaDark,                    // Deep Temple Saffron (#CC6600)
    onTertiary = Color.White,
    background = CleanCanvasBg,               // Crisp clean porcelain (#FAF7F5)
    onBackground = TextPrimaryDark,           // High contrast dark charcoal (#211510)
    surface = PureWhiteSurface,               // Pure white cards (#FFFFFF)
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantClean,     // Clean chip background (#F5ECE5)
    onSurfaceVariant = TextSecondaryMuted,
    outline = DividerClean
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkBhagwaPrimary,              // Deep Saffron (#FF9933)
    onPrimary = Color(0xFF381A00),
    primaryContainer = Color(0xFF804400),
    onPrimaryContainer = Color(0xFFFFDDB8),
    secondary = BhagwaLuminous,
    onSecondary = Color(0xFF452200),
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = Color(0xFFFFDDB8),
    tertiary = BhagwaDeepSaffron,
    onTertiary = Color.White,
    background = DarkCanvasBg,                // Deep obsidian (#140F0C)
    onBackground = DarkTextPrimary,
    surface = DarkSurfaceCard,                // Elevated card (#221A15)
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = Color(0xFF544238)
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
