package com.kingmotion.editor.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KingColors = darkColorScheme(
    primary = Color(0xFF9B8CFF),
    secondary = Color(0xFF54D6C5),
    background = Color(0xFF090A0F),
    surface = Color(0xFF13151D),
    surfaceVariant = Color(0xFF1C1F2A),
    onBackground = Color(0xFFF4F2FF),
    onSurface = Color(0xFFF4F2FF),
)

@Composable
fun KingMotionTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = KingColors, content = content)
}
