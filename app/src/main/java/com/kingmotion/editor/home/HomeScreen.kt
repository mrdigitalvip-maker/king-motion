package com.kingmotion.editor.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(onNewProject: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(MaterialTheme.colorScheme.background, MaterialTheme.colorScheme.surface))
        ).systemBarsPadding().padding(24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center,
            ) { Text("K", fontWeight = FontWeight.Black, fontSize = 24.sp) }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("KING MOTION", fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Text("Create without limits", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
            }
        }
        Spacer(Modifier.height(22.dp))
        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)).padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("1080p", "16:9", "30 FPS", "High").forEach { AssistChip(onClick = {}, label = { Text(it) }) }
        }
        Spacer(Modifier.height(28.dp))
        Text("Your next motion starts here.", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        Button(onClick = onNewProject, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp)) {
            Text("＋  New Project", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(36.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Recent projects", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            TextButton(onClick = {}) { Text("Assets & Effects  ·  Installed Packs") }
        }
        Box(
            Modifier.fillMaxWidth().weight(1f).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center,
        ) { Text("No projects yet", color = MaterialTheme.colorScheme.onSurface.copy(alpha = .55f)) }
    }
}
