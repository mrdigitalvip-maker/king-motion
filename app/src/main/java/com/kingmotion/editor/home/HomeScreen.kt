package com.kingmotion.editor.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kingmotion.engine.model.Project

@Composable fun HomeScreen(projects: List<Project>, onNewProject: () -> Unit, onOpen: (Project) -> Unit) {
    Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface))).systemBarsPadding().padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(44.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)), contentAlignment=Alignment.Center){Text("KM", fontWeight=FontWeight.Black)}; Spacer(Modifier.width(12.dp)); Column { Text("KING MOTION", fontWeight=FontWeight.Black); Text("Freedom • Control • Performance", style=MaterialTheme.typography.labelSmall) } }
        Spacer(Modifier.height(24.dp)); Text("Create without limits.", style=MaterialTheme.typography.headlineMedium, fontWeight=FontWeight.Bold)
        Spacer(Modifier.height(16.dp)); Button(onClick=onNewProject, modifier=Modifier.fillMaxWidth().height(54.dp)){Text("＋ New Project")}
        Spacer(Modifier.height(24.dp)); Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween){Text("Recent Projects", style=MaterialTheme.typography.titleMedium); Text("Assets & Effects", color=MaterialTheme.colorScheme.secondary)}
        Spacer(Modifier.height(8.dp)); if(projects.isEmpty()) Box(Modifier.fillMaxWidth().weight(1f).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)), contentAlignment=Alignment.Center){Text("No projects yet")}
        else LazyColumn(verticalArrangement=Arrangement.spacedBy(8.dp)){items(projects, key=Project::id){p -> Card(Modifier.fillMaxWidth().clickable{onOpen(p)}){Column(Modifier.padding(16.dp)){Text(p.name, fontWeight=FontWeight.Bold); val c=p.compositions.first(); Text("${c.width}×${c.height} • ${c.frameRate.toInt()} FPS • ${p.settings.quality}", style=MaterialTheme.typography.labelMedium)}}}}
    }
}
