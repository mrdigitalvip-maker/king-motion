package com.kingmotion.editor.projects

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kingmotion.engine.model.*

@Composable fun ProjectSetupScreen(onBack:()->Unit, onCreate:(Project)->Unit) {
    var name by remember{ mutableStateOf("") }; var resolution by remember{mutableStateOf("1080p")}; var ratio by remember{mutableStateOf(AspectRatio.LANDSCAPE_16_9)}; var fps by remember{mutableIntStateOf(30)}; var quality by remember{mutableStateOf(ProjectQuality.HIGH)}; var type by remember{mutableStateOf(ProjectType.VIDEO_EDIT)}; var background by remember{mutableStateOf(BackgroundType.BLACK)}
    Column(Modifier.fillMaxSize().systemBarsPadding().padding(20.dp).verticalScroll(rememberScrollState())) {
        TextButton(onClick=onBack){Text("‹ Back")}; Text("Project Setup", style=MaterialTheme.typography.headlineMedium)
        OutlinedTextField(name,{name=it},label={Text("Project name")},modifier=Modifier.fillMaxWidth()); Choice("Type", ProjectType.entries, type){type=it}; Choice("Resolution", listOf("1080p","1440p","2160p / 4K"), resolution){resolution=it}; Choice("Aspect", AspectRatio.entries, ratio){ratio=it}; Choice("FPS", listOf(24,30,48,60,120),fps){fps=it}; Choice("Quality",ProjectQuality.entries,quality){quality=it}; Choice("Background",BackgroundType.entries,background){background=it}
        Spacer(Modifier.height(18.dp)); Button(onClick={val base=when(resolution){"1440p"->1440;"2160p / 4K"->2160;else->1080}; val portrait=ratio in listOf(AspectRatio.VERTICAL_9_16,AspectRatio.PORTRAIT_4_5,AspectRatio.PORTRAIT_3_4); val dimensions=if(portrait) base to base*16/9 else base*16/9 to base; onCreate(ProjectFactory.create(name,dimensions.first,dimensions.second,fps.toFloat(),ProjectSettings(type,ratio,quality,background)))},modifier=Modifier.fillMaxWidth()){Text("Create Project")}
    }
}
@Composable private fun <T> Choice(label:String, options:List<T>, selected:T, change:(T)->Unit){Text(label,modifier=Modifier.padding(top=14.dp)); Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement=Arrangement.spacedBy(6.dp)){options.forEach{FilterChip(selected=it==selected,onClick={change(it)},label={Text(it.toString().replace('_',' '),maxLines=1)})}}}
