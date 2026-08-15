package com.kingmotion.editor.projects

import android.content.Context
import com.kingmotion.engine.model.*
import org.json.JSONArray
import org.json.JSONObject

/** Small versioned JSON repository. Media bytes never enter persistence; only durable content URIs do. */
class ProjectStore(context: Context) {
    private val prefs = context.getSharedPreferences("king_motion_projects", Context.MODE_PRIVATE)
    fun save(project: Project) { prefs.edit().putString(project.id, encode(project).toString()).putStringSet(INDEX, (prefs.getStringSet(INDEX, emptySet()) ?: emptySet()) + project.id).apply() }
    fun load(id: String): Project? = prefs.getString(id, null)?.let { runCatching { decode(JSONObject(it)) }.getOrNull() }
    fun list(): List<Project> = (prefs.getStringSet(INDEX, emptySet()) ?: emptySet()).mapNotNull(::load).sortedByDescending(Project::updatedAtEpochMs)

    private fun encode(p: Project) = JSONObject().put("schema", 1).put("id", p.id).put("name", p.name).put("created", p.createdAtEpochMs).put("updated", p.updatedAtEpochMs)
        .put("type", p.settings.type.name).put("ratio", p.settings.aspectRatio.name).put("quality", p.settings.quality.name).put("background", p.settings.background.name).put("argb", p.settings.backgroundArgb)
        .put("compositions", JSONArray(p.compositions.map(::encodeComposition)))
    private fun encodeComposition(c: Composition) = JSONObject().put("id", c.id).put("name", c.name).put("width", c.width).put("height", c.height).put("fps", c.frameRate).put("duration", c.durationUs)
        .put("layers", JSONArray(c.layers.map { l -> JSONObject().put("id", l.id).put("name", l.name).put("type", l.type.name).put("start", l.startTimeUs).put("duration", l.durationUs).put("order", l.order).put("visible", l.visible).put("locked", l.locked).put("muted", l.muted).put("volume", l.volume).put("uri", l.sourceUri).put("sourceIn", l.sourceInTimeUs) }))
        .put("beats", JSONArray(c.beatMarkers.map { b -> JSONObject().put("id", b.id).put("time", b.timestampUs).put("strength", b.strength).put("source", b.sourceLayerId) }))
    private fun decode(o: JSONObject): Project {
        val settings = ProjectSettings(ProjectType.valueOf(o.getString("type")), AspectRatio.valueOf(o.getString("ratio")), ProjectQuality.valueOf(o.getString("quality")), BackgroundType.valueOf(o.getString("background")), o.getLong("argb"))
        val array = o.getJSONArray("compositions")
        return Project(o.getString("id"), o.getString("name"), o.getLong("created"), o.getLong("updated"), settings, List(array.length()) { decodeComposition(array.getJSONObject(it)) })
    }
    private fun decodeComposition(o: JSONObject): Composition {
        val layers = o.getJSONArray("layers"); val beats = o.getJSONArray("beats")
        return Composition(o.getString("id"), o.getString("name"), o.getInt("width"), o.getInt("height"), o.getDouble("fps").toFloat(), o.getLong("duration"),
            List(layers.length()) { i -> layers.getJSONObject(i).let { Layer(id=it.getString("id"), name=it.getString("name"), type=LayerType.valueOf(it.getString("type")), startTimeUs=it.getLong("start"), durationUs=it.getLong("duration"), order=it.getInt("order"), visible=it.getBoolean("visible"), locked=it.getBoolean("locked"), muted=it.getBoolean("muted"), volume=it.getDouble("volume").toFloat(), sourceUri=it.optString("uri").takeIf(String::isNotEmpty), sourceInTimeUs=it.getLong("sourceIn")) } },
            List(beats.length()) { i -> beats.getJSONObject(i).let { BeatMarker(it.getString("id"), it.getLong("time"), it.getDouble("strength").toFloat(), it.optString("source").takeIf(String::isNotEmpty)) } })
    }
    private companion object { const val INDEX = "__index__" }
}
