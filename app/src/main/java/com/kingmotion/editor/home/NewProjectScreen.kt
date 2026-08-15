package com.kingmotion.editor.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kingmotion.engine.model.*

@Composable fun NewProjectScreen(onBack: () -> Unit, onCreate: (Project) -> Unit) {
    var name by remember { mutableStateOf("") }; var resolution by remember { mutableStateOf("1080p") }
    var ratio by remember { mutableStateOf("16:9") }; var fps by remember { mutableIntStateOf(30) }
    var quality by remember { mutableStateOf(ProjectQuality.HIGH) }; var type by remember { mutableStateOf(ProjectType.VIDEO_EDIT) }
    var background by remember { mutableStateOf(BackgroundType.BLACK) }
    val chips = @Composable { values: List<String>, selected: String, choose: (String)->Unit ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) { values.forEach { FilterChip(selected == it, { choose(it) }, { Text(it) }) } }
    }
    Scaffold(topBar = { TopAppBar(title={Text("New Project")}, navigationIcon={TextButton(onClick=onBack){Text("‹ Back")}}) }, bottomBar = {
        Button({ val base = when(resolution){"1440p"->2560 to 1440; "4K"->3840 to 2160; else->1920 to 1080}; val size=if(ratio=="9:16") base.second to base.first else base
            onCreate(ProjectFactory.create(name, ProjectConfiguration(size.first,size.second,fps,quality,background,type=type))) }, Modifier.fillMaxWidth().padding(16.dp).height(54.dp)) { Text("Create Project") }
    }) { pad -> LazyColumn(Modifier.padding(pad).padding(horizontal=16.dp), verticalArrangement=Arrangement.spacedBy(14.dp)) {
        item { OutlinedTextField(name,{name=it},Modifier.fillMaxWidth(),label={Text("Project Name")},placeholder={Text("King Motion Project 01")}) }
        item { Section("PROJECT TYPE"); chips(ProjectType.entries.map{it.name.replace('_',' ')}, type.name.replace('_',' ')){ type=ProjectType.valueOf(it.replace(' ','_')) } }
        item { Section("RESOLUTION"); chips(listOf("1080p","1440p","4K"),resolution){resolution=it} }
        item { Section("ASPECT RATIO"); chips(listOf("16:9","9:16","1:1","Custom"),ratio){ratio=it} }
        item { Section("FPS"); chips(listOf("24","30","48","60","120"),fps.toString()){fps=it.toInt()} }
        item { Section("QUALITY"); chips(ProjectQuality.entries.map{it.name},quality.name){quality=ProjectQuality.valueOf(it)} }
        item { Section("CANVAS / BACKGROUND"); chips(BackgroundType.entries.map{it.name},background.name){background=BackgroundType.valueOf(it)}; Text("Custom color · HEX / RGB ready", style=MaterialTheme.typography.bodySmall) }
        item { Text("Project format  •  $resolution  •  $ratio  •  $fps FPS  •  ${quality.name}", Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant,RoundedCornerShape(12.dp)).padding(14.dp)) }
    }}
}
@Composable private fun Section(text:String)=Text(text,style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.primary)
