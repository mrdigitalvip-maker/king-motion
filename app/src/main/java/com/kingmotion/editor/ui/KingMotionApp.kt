package com.kingmotion.editor.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kingmotion.editor.editor.EditorScreen
import com.kingmotion.editor.home.HomeScreen
import com.kingmotion.editor.home.NewProjectScreen
import com.kingmotion.engine.model.Project

private enum class Screen { Home, NewProject, Editor }

@Composable
fun KingMotionApp() {
    var screen by remember { mutableStateOf(Screen.Home) }
    var project by remember { mutableStateOf<Project?>(null) }
    when (screen) {
        Screen.Home -> HomeScreen(onNewProject = { screen = Screen.NewProject })
        Screen.NewProject -> NewProjectScreen(onBack = { screen = Screen.Home }) { project = it; screen = Screen.Editor }
        Screen.Editor -> project?.let { EditorScreen(it, onBack = { screen = Screen.Home }) }
    }
}
