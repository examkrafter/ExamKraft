package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF7FA3C9),
    onPrimary = Color(0xFF071E36),
    primaryContainer = NavyPrimary,
    onPrimaryContainer = Color(0xFFD3E3F5),
    secondary = GoldAccent,
    onSecondary = Color(0xFF382600),
    secondaryContainer = Color(0xFF5A4100),
    onSecondaryContainer = GoldContainer,
    tertiary = Color(0xFF81C784),
    background = SurfaceDark,
    surface = CardDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4F5),
    onPrimaryContainer = NavyPrimary,
    secondary = GoldAccent,
    onSecondary = Color.White,
    secondaryContainer = GoldContainer,
    onSecondaryContainer = GoldOnContainer,
    tertiary = GreenSuccess,
    background = SurfaceLight,
    surface = CardLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    outline = OutlineLight
)

@Composable
fun ExamKraftTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
