package com.traveler.schemejump.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF185FA5),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE6F1FB),
    onPrimaryContainer = Color(0xFF0C447C),
    secondary = Color(0xFF0F6E56),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE1F5EE),
    onSecondaryContainer = Color(0xFF085041),
    background = Color(0xFFF6F7F9),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFF1F3F5),
    onSurfaceVariant = Color(0xFF44474A),
    outline = Color(0xFFB4B2A9)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF85B7EB),
    onPrimary = Color(0xFF042C53),
    primaryContainer = Color(0xFF0C447C),
    onPrimaryContainer = Color(0xFFE6F1FB),
    secondary = Color(0xFF5DCAA5),
    onSecondary = Color(0xFF04342C),
    secondaryContainer = Color(0xFF085041),
    onSecondaryContainer = Color(0xFFE1F5EE),
    background = Color(0xFF14161A),
    onBackground = Color(0xFFE3E7EA),
    surface = Color(0xFF1B1E22),
    onSurface = Color(0xFFE3E7EA),
    surfaceVariant = Color(0xFF262A2F),
    onSurfaceVariant = Color(0xFFC2C7CC),
    outline = Color(0xFF5F6A72)
)

@Composable
fun SchemeJumpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
