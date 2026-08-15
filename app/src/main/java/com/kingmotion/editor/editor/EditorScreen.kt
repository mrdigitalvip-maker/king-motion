package com.kingmotion.editor.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EditorScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).systemBarsPadding()) {
        Row(Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("‹ Projects") }
            Spacer(Modifier.weight(1f)); Text("Untitled", style = MaterialTheme.typography.titleMedium); Spacer(Modifier.weight(1f))
            TextButton(onClick = {}) { Text("Export") }
        }
        Box(Modifier.fillMaxWidth().weight(1f).padding(12.dp).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
            Text("PREVIEW", color = MaterialTheme.colorScheme.onSurface.copy(alpha = .45f))
        }
        Column(Modifier.fillMaxWidth().height(190.dp).background(MaterialTheme.colorScheme.surface).padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("00:00.00"); Text("TIMELINE"); Text("00:10.00") }
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().height(42.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = .3f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.CenterStart) { Text("  Layer 1") }
        }
        Row(Modifier.fillMaxWidth().height(72.dp).padding(horizontal = 10.dp), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
            listOf("Media", "Text", "Shape", "Effect", "Audio").forEach { TextButton(onClick = {}) { Text(it) } }
        }
    }
}
