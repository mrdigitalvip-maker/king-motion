package com.kingmotion.editor.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kingmotion.editor.editor.EditorScreen
import com.kingmotion.editor.home.HomeScreen

private enum class Screen { Home, Editor }

@Composable
fun KingMotionApp() {
    var screen by remember { mutableStateOf(Screen.Home) }
    when (screen) {
        Screen.Home -> HomeScreen(onNewProject = { screen = Screen.Editor })
        Screen.Editor -> EditorScreen(onBack = { screen = Screen.Home })
    }
}
