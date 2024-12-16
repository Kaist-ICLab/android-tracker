package kaist.iclab.field_tracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2196F3),     // Blue primary color for the switch
    onPrimary = Color.White,          // White on top of primary
    secondary = Color(0xFFB0BEC5),    // Light gray for secondary/inactive states
    background = Color(0xFFF5F5F5),   // Light background
    surface = Color.White,            // Surface color for light theme
    error = Color(0xFFD32F2F)         // Material recommended error color
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2962FF),      // Blue primary color for dark theme
    onPrimary = Color.White,
    secondary = Color(0xFFB0BEC5),
    background = Color(0xFF121212),   // Dark background
    surface = Color(0xFF121212),
    error = Color(0xFFD32F2F)
)

@Composable
fun TrackerTheme(
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}