package com.kingmotion.editor.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.kingmotion.editor.dashboard.ProjectHomeScreen
import com.kingmotion.editor.projects.ProjectSetupScreen
import com.kingmotion.editor.projects.ProjectStore
import com.kingmotion.editor.studio.EditorStudioScreen
import com.kingmotion.engine.model.Project

private sealed interface AppDestination {
    data object Home : AppDestination
    data object Setup : AppDestination
    data class Studio(val project: Project) : AppDestination
}

@Composable
fun KingMotionRoot() {
    val context = LocalContext.current
    val store = remember { ProjectStore(context) }
    var destination by remember { mutableStateOf<AppDestination>(AppDestination.Home) }

    BackHandler(destination !is AppDestination.Home) { destination = AppDestination.Home }
    when (val current = destination) {
        AppDestination.Home -> ProjectHomeScreen(
            projects = store.list(),
            onNewProject = { destination = AppDestination.Setup },
            onOpen = { destination = AppDestination.Studio(it) },
        )
        AppDestination.Setup -> ProjectSetupScreen(
            onBack = { destination = AppDestination.Home },
            onCreate = { project ->
                store.save(project)
                destination = AppDestination.Studio(project)
            },
        )
        is AppDestination.Studio -> EditorStudioScreen(
            initial = current.project,
            onBack = { destination = AppDestination.Home },
            onSave = store::save,
        )
    }
}
