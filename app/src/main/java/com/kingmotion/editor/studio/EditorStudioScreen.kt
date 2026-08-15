package com.kingmotion.editor.studio

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.kingmotion.engine.effects.*
import com.kingmotion.engine.keyframes.AnimatedFloat
import com.kingmotion.engine.keyframes.Keyframe
import com.kingmotion.engine.media.*
import com.kingmotion.engine.model.*
import com.kingmotion.engine.timeline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private enum class ImportKind(val mime:String){VIDEO("video/*"),IMAGE("image/*"),AUDIO("audio/*")}
private enum class PreviewQuality { AUTO, QUARTER, HALF, FULL }

@Composable fun EditorStudioScreen(initial: Project, onBack:()->Unit, onSave:(Project)->Unit) {
    val context=LocalContext.current; var project by remember{mutableStateOf(initial)}; var composition by remember{mutableStateOf(initial.compositions.first())}; var selectedId by remember{mutableStateOf<String?>(null)}; var playhead by remember{mutableLongStateOf(0)}; var zoom by remember{mutableFloatStateOf(72f)}; var loop by remember{mutableStateOf(false)}; var quality by remember{mutableStateOf(PreviewQuality.AUTO)}; var message by remember{mutableStateOf<String?>(null)}; var saving by remember{mutableStateOf(false)}; var pendingKind by remember{mutableStateOf(ImportKind.VIDEO)}
    val history=remember(initial.id){EditHistory(composition)}; val scope=rememberCoroutineScope()
    val player=remember{ExoPlayer.Builder(context).build()}; DisposableEffect(Unit){onDispose{player.release()}}
    fun apply(next:Composition){composition=history.execute(next); project=project.copy(updatedAtEpochMs=System.currentTimeMillis(),compositions=listOf(composition)); saving=true}
    val picker=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()){uri:Uri? -> if(uri!=null){runCatching{context.contentResolver.takePersistableUriPermission(uri,android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)}; val kind=pendingKind; scope.launch { val meta=withContext(Dispatchers.IO){readMetadata(context,uri)}; val validation = if (kind == ImportKind.VIDEO) VideoImportPolicy().validate(meta) else MediaValidation.Accepted; if(validation is MediaValidation.Rejected){message=validation.reason}else{val type=when(kind){ImportKind.VIDEO->LayerType.VIDEO;ImportKind.IMAGE->LayerType.IMAGE;ImportKind.AUDIO->LayerType.AUDIO}; val duration=meta.durationUs.takeIf{it>0}?:5_000_000; val layer=Layer(name=uri.lastPathSegment?.substringAfterLast('/')?:type.name,type=type,startTimeUs=playhead,durationUs=duration,order=composition.layers.size,sourceUri=uri.toString()); apply(TimelineEditor.addLayer(composition,layer));selectedId=layer.id} }}}
    LaunchedEffect(composition){if(saving){delay(700); val saved=project.copy(compositions=listOf(composition)); onSave(saved);saving=false}}
    LaunchedEffect(Unit){while(true){if(player.isPlaying)playhead=player.currentPosition*1000;delay(33)}}
    LaunchedEffect(loop){player.repeatMode=if(loop) androidx.media3.common.Player.REPEAT_MODE_ONE else androidx.media3.common.Player.REPEAT_MODE_OFF}
    LaunchedEffect(selectedId){val layer=composition.layers.firstOrNull{it.id==selectedId&&it.sourceUri!=null&&it.type in listOf(LayerType.VIDEO,LayerType.AUDIO)}; if(layer!=null){player.setMediaItem(MediaItem.fromUri(layer.sourceUri!!),layer.sourceInTimeUs/1000);player.prepare()}}
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).systemBarsPadding()){
        Row(Modifier.fillMaxWidth().height(52.dp).padding(horizontal=6.dp),verticalAlignment=Alignment.CenterVertically){TextButton(onClick=onBack){Text("‹")};Text(project.name,modifier=Modifier.weight(1f));TextButton(enabled=history.canUndo,onClick={composition=history.undo();project=project.copy(updatedAtEpochMs=System.currentTimeMillis(),compositions=listOf(composition));saving=true}){Text("Undo")};TextButton(enabled=history.canRedo,onClick={composition=history.redo();project=project.copy(updatedAtEpochMs=System.currentTimeMillis(),compositions=listOf(composition));saving=true}){Text("Redo")};Text("${composition.width}×${composition.height}  ${composition.frameRate.toInt()}fps",style=MaterialTheme.typography.labelSmall);TextButton(onClick={quality=PreviewQuality.entries[(quality.ordinal+1)%PreviewQuality.entries.size]}){Text(quality.name)};TextButton(onClick={message="Export is prepared but rendering is not implemented yet."}){Text("Export")}}
        Row(Modifier.weight(1f).fillMaxWidth().padding(8.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){
            Box(Modifier.weight(1f).fillMaxHeight().background(Color.Black,RoundedCornerShape(10.dp)),contentAlignment=Alignment.Center){if(player.mediaItemCount>0)AndroidView({PlayerView(it).apply{useController=false;this.player=player}},Modifier.fillMaxSize()) else Text("Import and select media to preview",color=Color.Gray)}
            Column(Modifier.widthIn(min=150.dp,max=230.dp).verticalScroll(rememberScrollState())){Text("TOOLS",style=MaterialTheme.typography.labelSmall); listOf(ImportKind.VIDEO,ImportKind.IMAGE,ImportKind.AUDIO).forEach{k->TextButton(onClick={pendingKind=k;picker.launch(arrayOf(k.mime))}){Text("Import ${k.name.lowercase()}")}}; EditorActions(composition,selectedId,playhead,{apply(it)},{selectedId=it},{message=it})}
        }
        PlaybackBar(playhead,composition.frameRate,loop,{if(player.isPlaying)player.pause()else player.play()},{delta->playhead=(playhead+delta).coerceAtLeast(0);player.seekTo(playhead/1000)},{loop=it},composition.beatMarkers)
        Timeline(composition,selectedId,playhead,zoom,{selectedId=it},{playhead=it;player.seekTo(it/1000)},{zoom=(zoom+it).coerceIn(24f,240f)})
        Text(if(saving)"Saving…" else "Saved",Modifier.padding(horizontal=12.dp,vertical=2.dp),style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.secondary)
    }
    if(message!=null)AlertDialog(onDismissRequest={message=null},confirmButton={TextButton(onClick={message=null}){Text("OK")}},title={Text("King Motion")},text={Text(message!!)})
}

@Composable private fun EditorActions(c:Composition,id:String?,time:Long,apply:(Composition)->Unit,select:(String?)->Unit,message:(String)->Unit){val layer=c.layers.firstOrNull{it.id==id}; Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(2.dp)){Button(onClick={if(layer!=null)apply(TimelineEditor.split(c,layer.id,time))},enabled=layer!=null){Text("Split")};Button(onClick={if(layer!=null)apply(TimelineEditor.duplicate(c,layer.id))},enabled=layer!=null){Text("Copy")}}; Row{TextButton(onClick={if(layer!=null){apply(TimelineEditor.delete(c,layer.id));select(null)}},enabled=layer!=null){Text("Delete")};TextButton(onClick={if(layer!=null)apply(TimelineEditor.move(c,layer.id,-1))},enabled=layer!=null){Text("↑")};TextButton(onClick={if(layer!=null)apply(TimelineEditor.move(c,layer.id,1))},enabled=layer!=null){Text("↓")}}
    if(layer?.type==LayerType.VIDEO)Button(onClick={val audio=layer.copy(id=java.util.UUID.randomUUID().toString(),name="${layer.name} audio",type=LayerType.AUDIO,order=c.layers.size,muted=false);apply(TimelineEditor.addLayer(c,audio));message("Audio track linked locally. The video container is used as the non-destructive audio source.")}){Text("Extract Audio")}
    if(layer?.type==LayerType.AUDIO){Button(onClick={message("PCM decoding adapter is not complete; automatic analysis is unavailable in this build.")}){Text("Detect Beats")};Button(onClick={apply(c.copy(beatMarkers=(c.beatMarkers+BeatMarker(timestampUs=time,sourceLayerId=layer.id)).sortedBy{it.timestampUs}))}){Text("Add Beat")}}
    if(layer!=null&&layer.type!=LayerType.AUDIO){Text("Transform",style=MaterialTheme.typography.titleSmall);TransformSlider("Opacity",layer.transform.opacity.initialValue,0f..1f){v->apply(TimelineEditor.update(c,layer.id){it.copy(transform=it.transform.copy(opacity=AnimatedFloat(v)))})};Button(onClick={apply(TimelineEditor.update(c,layer.id){it.copy(transform=it.transform.copy(opacity=it.transform.opacity.copy(keyframes=it.transform.opacity.keyframes+Keyframe(time,it.transform.opacity.valueAt(time)))))})}){Text("Add keyframe")};Button(onClick={val brightness=EffectInstance("com.kingmotion.effects.brightness",parameters=listOf(EffectParameter("brightness",AnimatedFloat(0f))));apply(TimelineEditor.update(c,layer.id){it.copy(effects=it.effects+brightness)})}){Text("+ Brightness (Color)")}}
}
@Composable private fun TransformSlider(name:String,value:Float,range:ClosedFloatingPointRange<Float>,change:(Float)->Unit){Text("$name ${(value*100).toInt()}%",style=MaterialTheme.typography.labelSmall);Slider(value,change,valueRange=range)}
@Composable private fun PlaybackBar(time:Long,fps:Float,loop:Boolean,toggle:()->Unit,step:(Long)->Unit,setLoop:(Boolean)->Unit,beats:List<BeatMarker>){Row(Modifier.fillMaxWidth().height(46.dp),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.Center){TextButton(onClick={step(-(1_000_000/fps).toLong())}){Text("|‹")};Button(onClick=toggle){Text("Play / Pause")};TextButton(onClick={step((1_000_000/fps).toLong())}){Text("›|")};FilterChip(loop,onClick={setLoop(!loop)},label={Text("Loop")});Spacer(Modifier.width(12.dp));Text(formatTime(time)); if(beats.isNotEmpty())Text("  Beats ${beats.size}",color=Color.Magenta)}}
@Composable private fun Timeline(c:Composition,selected:String?,playhead:Long,zoom:Float,select:(String)->Unit,seek:(Long)->Unit,changeZoom:(Float)->Unit){val scroll=rememberScrollState();Column(Modifier.fillMaxWidth().height(205.dp).background(MaterialTheme.colorScheme.surface).padding(6.dp)){Row(verticalAlignment=Alignment.CenterVertically){Text("TIMELINE",Modifier.weight(1f));TextButton(onClick={changeZoom(-16f)}){Text("−")};Text("${zoom.toInt()} px/s");TextButton(onClick={changeZoom(16f)}){Text("+")}};Box(Modifier.fillMaxSize().horizontalScroll(scroll).pointerInput(zoom){detectTapGestures{seek((it.x/zoom*1_000_000).toLong())}}){val width=maxOf(800f,c.durationUs/1_000_000f*zoom).dp;Column(Modifier.width(width)){Canvas(Modifier.fillMaxWidth().height(24.dp)){c.beatMarkers.forEach{b->val x=b.timestampUs/1_000_000f*zoom;drawLine(Color.Magenta,Offset(x,0f),Offset(x,size.height),2f)}};c.layers.sortedByDescending{it.order}.forEach{l->Row(Modifier.height(30.dp)){Spacer(Modifier.width((l.startTimeUs/1_000_000f*zoom).dp));Box(Modifier.width((l.durationUs/1_000_000f*zoom).coerceAtLeast(30f).dp).fillMaxHeight().background(if(l.id==selected)MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,RoundedCornerShape(4.dp)).clickable{select(l.id)},contentAlignment=Alignment.CenterStart){Text(" ${l.type}: ${l.name}",maxLines=1)}}}};Canvas(Modifier.matchParentSize()){val x=playhead/1_000_000f*zoom;drawLine(Color.White,Offset(x,0f),Offset(x,size.height),2f)}}}}
private fun formatTime(us:Long):String{val ms=us/1000;return "%02d:%02d:%02d.%03d".format(ms/3_600_000,(ms/60_000)%60,(ms/1000)%60,ms%1000)}
private fun readMetadata(context:android.content.Context,uri:Uri):MediaInfo{val r=MediaMetadataRetriever();return try{r.setDataSource(context,uri);MediaInfo(r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull()?:0,r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull()?:0,(r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLongOrNull()?:0)*1000,r.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)=="yes")}finally{r.release()}}
