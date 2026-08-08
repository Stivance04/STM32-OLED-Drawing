package com.stivance.drawsync.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DrawSyncDarkColorScheme = darkColorScheme(
    primary = Color(0xFF7C4DFF),
    onPrimary = Color.White,

    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,

    tertiary = Color(0xFFFF4081),
    onTertiary = Color.White,

    background = Color(0xFF0B0B0F),
    onBackground = Color(0xFFF5F5F5),

    surface = Color(0xFF0B0B0F),
    onSurface = Color(0xFFF5F5F5),

    surfaceVariant = Color(0xFF1A1A21),
    onSurfaceVariant = Color(0xFFB8B8C2),

    outline = Color(0xFF3A3A44)
)

@Composable
fun DrawSyncTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DrawSyncDarkColorScheme,
        typography = Typography,
        content = content
    )
}