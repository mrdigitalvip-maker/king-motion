package com.kingmotion.editor.editor

import com.kingmotion.engine.model.Project

enum class PreviewQuality(val label: String) { AUTO("Auto"), QUARTER("1/4"), HALF("1/2"), FULL("Full") }
enum class StudioPanel { NONE, MEDIA, AUDIO, TEXT, SHAPE, EFFECTS, ASSETS, ADJUST, TRANSFORM, SPEED, MASK, ANIMATION, PROPERTIES, SETTINGS }
enum class LayerType { VIDEO, IMAGE, AUDIO, TEXT, SHAPE, SOLID, ADJUSTMENT, COMPOSITION }
data class StudioLayer(val id: Int, val name: String, val type: LayerType, val startMs: Long = 0, val durationMs: Long = 10_000, val visible: Boolean = true, val locked: Boolean = false, val muted: Boolean = false)
data class TimelineState(val playheadMs: Long = 0, val zoom: Float = 1f)
data class PlaybackState(val playing: Boolean = false, val looping: Boolean = false, val quality: PreviewQuality = PreviewQuality.AUTO)
data class EditorState(val project: Project, val layers: List<StudioLayer> = listOf(StudioLayer(1, "Main video", LayerType.VIDEO), StudioLayer(2, "Title", LayerType.TEXT, 1000, 5000)), val selectedLayerId: Int? = 1, val panel: StudioPanel = StudioPanel.NONE, val timeline: TimelineState = TimelineState(), val playback: PlaybackState = PlaybackState())

sealed interface EditorAction {
    data class SelectLayer(val id: Int) : EditorAction
    data class OpenPanel(val panel: StudioPanel) : EditorAction
    data class Seek(val timeMs: Long) : EditorAction
    data class SetZoom(val zoom: Float) : EditorAction
    data object TogglePlayback : EditorAction
    data object ToggleLoop : EditorAction
    data object DeleteSelected : EditorAction
    data object DuplicateSelected : EditorAction
}

class EditorStateHolder(initial: Project) {
    var state = androidx.compose.runtime.mutableStateOf(EditorState(initial)); private set
    fun dispatch(action: EditorAction) { state.value = reduce(state.value, action) }
    companion object {
        fun reduce(s: EditorState, a: EditorAction): EditorState = when (a) {
            is EditorAction.SelectLayer -> s.copy(selectedLayerId = a.id, panel = StudioPanel.PROPERTIES)
            is EditorAction.OpenPanel -> s.copy(panel = a.panel)
            is EditorAction.Seek -> s.copy(timeline = s.timeline.copy(playheadMs = a.timeMs.coerceIn(0, 10_000)))
            is EditorAction.SetZoom -> s.copy(timeline = s.timeline.copy(zoom = a.zoom.coerceIn(.5f, 4f)))
            EditorAction.TogglePlayback -> s.copy(playback = s.playback.copy(playing = !s.playback.playing))
            EditorAction.ToggleLoop -> s.copy(playback = s.playback.copy(looping = !s.playback.looping))
            EditorAction.DeleteSelected -> s.copy(layers = s.layers.filterNot { it.id == s.selectedLayerId }, selectedLayerId = null)
            EditorAction.DuplicateSelected -> s.layers.find { it.id == s.selectedLayerId }?.let { layer ->
                val next = (s.layers.maxOfOrNull { it.id } ?: 0) + 1
                s.copy(layers = s.layers + layer.copy(id = next, name = "${layer.name} copy"), selectedLayerId = next)
            } ?: s
        }
    }
}
