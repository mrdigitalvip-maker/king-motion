package com.kingmotion.editor.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kingmotion.engine.model.Project

@Composable fun EditorScreen(project: Project, onBack: () -> Unit) {
    val holder = remember(project.id) { EditorStateHolder(project) }; val state by holder.state
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).systemBarsPadding()) {
        TopBar(state, onBack) { holder.dispatch(it) }
        Preview(state) { holder.dispatch(it) }
        Timeline(state) { holder.dispatch(it) }
        state.selectedLayerId?.let { ContextBar { holder.dispatch(it) } }
        ToolBar { holder.dispatch(EditorAction.OpenPanel(it)) }
    }
    if (state.panel != StudioPanel.NONE) ModalBottomSheet(onDismissRequest={holder.dispatch(EditorAction.OpenPanel(StudioPanel.NONE))}) { Panel(state) { holder.dispatch(it) } }
}

@Composable private fun TopBar(s:EditorState, back:()->Unit, action:(EditorAction)->Unit) = Row(Modifier.fillMaxWidth().height(54.dp).padding(horizontal=6.dp),verticalAlignment=Alignment.CenterVertically) {
    TextButton(back){Text("‹")}; Column(Modifier.weight(1f)){Text(s.project.name,style=MaterialTheme.typography.titleSmall); Text("${s.project.configuration.width}×${s.project.configuration.height} · ${s.project.configuration.frameRate} FPS",style=MaterialTheme.typography.labelSmall)}
    TextButton({}){Text("↶")}; TextButton({}){Text("↷")}; TextButton({action(EditorAction.OpenPanel(StudioPanel.SETTINGS))}){Text("⚙")}; Button({}){Text("Export")}
}
@Composable private fun Preview(s:EditorState, action:(EditorAction)->Unit) = Column(Modifier.fillMaxWidth().weight(1f).padding(horizontal=10.dp)) {
    Box(Modifier.fillMaxWidth().weight(1f).background(Color(0xff090b10),RoundedCornerShape(12.dp)),contentAlignment=Alignment.Center){ Box(Modifier.fillMaxHeight(.72f).aspectRatio(s.project.configuration.width.toFloat()/s.project.configuration.height).background(Color(0xff171b24)),contentAlignment=Alignment.Center){Text("COMPOSITION CANVAS",color=Color.Gray)} }
    Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceEvenly,verticalAlignment=Alignment.CenterVertically){TextButton({action(EditorAction.Seek((s.timeline.playheadMs-33).coerceAtLeast(0)))}){Text("|◀")}; TextButton({action(EditorAction.TogglePlayback)}){Text(if(s.playback.playing)"Pause" else "Play")}; TextButton({action(EditorAction.Seek(s.timeline.playheadMs+33))}){Text("▶|")}; TextButton({action(EditorAction.ToggleLoop)}){Text(if(s.playback.looping)"Loop ✓" else "Loop")}; Text("00:00:${(s.timeline.playheadMs/10).toString().padStart(3,'0')}"); TextButton({}){Text("⛶")}}
}
@Composable private fun Timeline(s:EditorState, action:(EditorAction)->Unit) = Column(Modifier.fillMaxWidth().height(210.dp).background(MaterialTheme.colorScheme.surface).padding(8.dp)) {
    Row(verticalAlignment=Alignment.CenterVertically){Text("TIMELINE",style=MaterialTheme.typography.labelLarge); Spacer(Modifier.weight(1f)); TextButton({action(EditorAction.SetZoom(s.timeline.zoom-.25f))}){Text("−")}; Text("${(s.timeline.zoom*100).toInt()}%"); TextButton({action(EditorAction.SetZoom(s.timeline.zoom+.25f))}){Text("+")}; Button({action(EditorAction.OpenPanel(StudioPanel.MEDIA))}){Text("＋ Layer")}}
    Slider(s.timeline.playheadMs/10000f,{action(EditorAction.Seek((it*10000).toLong()))}); Text("0s     2s     4s     6s     8s     10s",style=MaterialTheme.typography.labelSmall)
    LazyColumn { items(s.layers,key={it.id}) { layer -> Row(Modifier.fillMaxWidth().padding(vertical=2.dp).background(if(layer.id==s.selectedLayerId) MaterialTheme.colorScheme.primary.copy(.25f) else MaterialTheme.colorScheme.surfaceVariant,RoundedCornerShape(6.dp)).clickable{action(EditorAction.SelectLayer(layer.id))}.padding(8.dp)){Text("◉  🔒  ${layer.type.name}",Modifier.width(125.dp),style=MaterialTheme.typography.labelSmall); Text(layer.name); Spacer(Modifier.weight(1f)); if(layer.type==LayerType.AUDIO) Text("▁▃▆▂▅") } } }
}
@Composable private fun ContextBar(action:(EditorAction)->Unit)=Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(3.dp)){listOf("Split","Duplicate","Delete","Replace","Copy","Paste","Move Up","Move Down").forEach { label->TextButton({when(label){"Duplicate"->action(EditorAction.DuplicateSelected);"Delete"->action(EditorAction.DeleteSelected);else->Unit}}){Text(label)} }}
@Composable private fun ToolBar(open:(StudioPanel)->Unit)=Row(Modifier.fillMaxWidth().height(62.dp).horizontalScroll(rememberScrollState()),verticalAlignment=Alignment.CenterVertically){listOf("Media" to StudioPanel.MEDIA,"Audio" to StudioPanel.AUDIO,"Text" to StudioPanel.TEXT,"Shape" to StudioPanel.SHAPE,"Effects" to StudioPanel.EFFECTS,"Assets" to StudioPanel.ASSETS,"Adjust" to StudioPanel.ADJUST,"Transform" to StudioPanel.TRANSFORM,"Speed" to StudioPanel.SPEED,"Mask" to StudioPanel.MASK,"Animation" to StudioPanel.ANIMATION).forEach{(n,p)->TextButton({open(p)}){Text(n)}}}
@Composable private fun Panel(s:EditorState, action:(EditorAction)->Unit){Column(Modifier.fillMaxWidth().padding(20.dp).navigationBarsPadding()){Text(s.panel.name,style=MaterialTheme.typography.titleLarge);Spacer(Modifier.height(12.dp));when(s.panel){
    StudioPanel.MEDIA->Text("Search media  ·  Videos  Images  Audio  Files  Assets\n＋ Import from device (1080p minimum prepared)")
    StudioPanel.AUDIO->Text("Import Audio  ·  Extract Audio\nWaveform  ▂▅▇▃▆  ·  Volume  Fade in  Fade out  Mute  Speed  Split")
    StudioPanel.EFFECTS->Text("＋ Add Effect\nBrightness\nColor · Blur · Distortion · Glitch · Transform · Light · Stylize · Noise · Motion · Utility")
    StudioPanel.ASSETS->Text("Effects · Presets · Transitions · Overlays · Shapes · Fonts · CC · Shaders · Packs")
    StudioPanel.SETTINGS->Text("Canvas Size  ${s.project.configuration.width} × ${s.project.configuration.height}\nResolution · ${s.project.configuration.aspectRatio} · ${s.project.configuration.frameRate} FPS\nBackground Color · Preview Quality: ${s.playback.quality.label}\nProject Duration  00:10.000")
    StudioPanel.PROPERTIES,StudioPanel.TRANSFORM->Text("TRANSFORM   ◇ Keyframe  ◀  ▶\nPosition X    0     Position Y    0\nScale X       100   Scale Y       100\nRotation      0°    Opacity       100%\n\nBLEND\nBlend Mode    Normal    Opacity 100%\n\nTIMING\nStart 00:00 · Duration 00:10 · Speed 1.0×")
    else->Text("${s.panel.name.lowercase().replaceFirstChar{it.uppercase()}} tools are structured and ready for the processing engine.")};Spacer(Modifier.height(24.dp))}}
