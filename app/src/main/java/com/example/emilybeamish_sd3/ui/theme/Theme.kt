// Theme.kt
package com.example.emilybeamish_sd3.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

// Add ThemeType enum here
enum class ThemeType {
    DEFAULT,
    PINK,
    LIGHT_PURPLE,
    DARK_PURPLE
}

// Default Material 3 color schemes
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

// PINK THEME - LIGHT ONLY
private val PinkLightColorScheme = lightColorScheme(
    primary = Pink1,
    secondary = Pink2,
    tertiary = Pink3,
    background = PinkBackground
)

// LIGHT PURPLE THEME - LIGHT ONLY
private val LightPurpleLightColorScheme = lightColorScheme(
    primary = LightPurple1,
    secondary = LightPurple2,
    tertiary = LightPurple3,
    background = LightPurpleBackground
)

// DARK PURPLE THEME - DARK ONLY
private val DarkPurpleColorScheme = darkColorScheme(
    primary = DarkPurple1,
    secondary = DarkPurple2,
    tertiary = DarkPurple3,
    background = DarkPurpleBackground
)

// Theme state holder
class ThemeState {
    var currentTheme by mutableStateOf(ThemeType.DEFAULT)
}

@Composable
fun rememberThemeState(): ThemeState {
    return remember { ThemeState() }
}

@Composable
fun Emilybeamish_SD3Theme(
    themeType: ThemeType = ThemeType.DEFAULT,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeType) {
        ThemeType.PINK -> PinkLightColorScheme
        ThemeType.LIGHT_PURPLE -> LightPurpleLightColorScheme
        ThemeType.DARK_PURPLE -> DarkPurpleColorScheme
        ThemeType.DEFAULT -> {
            when {
                dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val context = LocalContext.current
                    if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                }
                darkTheme -> DarkColorScheme
                else -> LightColorScheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}