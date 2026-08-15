package com.kingmotion.editor.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.kingmotion.editor.editor.EditorScreen
import com.kingmotion.editor.home.HomeScreen
import com.kingmotion.editor.projects.ProjectSetupScreen
import com.kingmotion.editor.projects.ProjectStore
import com.kingmotion.engine.model.Project

private sealed interface Screen {
    data object Home : Screen
    data object Setup : Screen
    data class Editor(val project: Project) : Screen
}

@Composable
fun KingMotionApp() {
    val context = LocalContext.current
    val store = remember { ProjectStore(context) }
    var screen by remember { mutableStateOf<Screen>(Screen.Home) }
    BackHandler(screen !is Screen.Home) { screen = Screen.Home }
    when (val current = screen) {
        Screen.Home -> HomeScreen(
            projects = store.list(),
            onNewProject = { screen = Screen.Setup },
            onOpen = { screen = Screen.Editor(it) },
        )
        Screen.Setup -> ProjectSetupScreen(onBack = { screen = Screen.Home }) { project ->
            store.save(project)
            screen = Screen.Editor(project)
        }
        is Screen.Editor -> EditorScreen(
            initial = current.project,
            onBack = { screen = Screen.Home },
            onSave = store::save,
        )
    }
}
