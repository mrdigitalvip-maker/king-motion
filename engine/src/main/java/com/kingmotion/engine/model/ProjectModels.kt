package com.kingmotion.engine.model

import com.kingmotion.engine.effects.EffectInstance
import com.kingmotion.engine.keyframes.AnimatedFloat
import java.util.UUID

enum class ProjectType { VIDEO_EDIT, MOTION_COMPOSITION, EMPTY }
enum class ProjectQuality { STANDARD, HIGH, ULTRA, MAXIMUM }
enum class AspectRatio { VERTICAL_9_16, LANDSCAPE_16_9, SQUARE_1_1, PORTRAIT_4_5, PORTRAIT_3_4, LANDSCAPE_4_3, CUSTOM }
enum class BackgroundType { BLACK, WHITE, GRAY, TRANSPARENT, CUSTOM }
enum class LayerType { VIDEO, IMAGE, AUDIO, TEXT, SHAPE, SOLID, ADJUSTMENT, COMPOSITION }

data class ProjectSettings(
    val type: ProjectType = ProjectType.VIDEO_EDIT,
    val aspectRatio: AspectRatio = AspectRatio.LANDSCAPE_16_9,
    val quality: ProjectQuality = ProjectQuality.HIGH,
    val background: BackgroundType = BackgroundType.BLACK,
    val backgroundArgb: Long = 0xff000000,
)

data class Transform(
    val positionX: AnimatedFloat = AnimatedFloat(0f),
    val positionY: AnimatedFloat = AnimatedFloat(0f),
    val scaleX: AnimatedFloat = AnimatedFloat(1f),
    val scaleY: AnimatedFloat = AnimatedFloat(1f),
    val rotation: AnimatedFloat = AnimatedFloat(0f),
    val opacity: AnimatedFloat = AnimatedFloat(1f),
)

data class BeatMarker(
    val id: String = UUID.randomUUID().toString(),
    val timestampUs: Long,
    val strength: Float = 1f,
    val sourceLayerId: String? = null,
)

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long,
    val settings: ProjectSettings = ProjectSettings(),
    val compositions: List<Composition>,
)

data class Composition(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val width: Int,
    val height: Int,
    val frameRate: Float,
    val durationUs: Long,
    val layers: List<Layer>,
    val beatMarkers: List<BeatMarker> = emptyList(),
)

data class Layer(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: LayerType,
    val startTimeUs: Long,
    val durationUs: Long,
    val order: Int,
    val visible: Boolean = true,
    val locked: Boolean = false,
    val muted: Boolean = false,
    val volume: Float = 1f,
    val transform: Transform = Transform(),
    val effects: List<EffectInstance> = emptyList(),
    val sourceUri: String? = null,
    val sourceInTimeUs: Long = 0,
) {
    fun contains(timeUs: Long) = timeUs in startTimeUs until (startTimeUs + durationUs)
    val endTimeUs get() = startTimeUs + durationUs
}

object ProjectFactory {
    fun create(
        name: String,
        width: Int,
        height: Int,
        fps: Float,
        settings: ProjectSettings = ProjectSettings(),
        projectNumber: Int = 1,
        nowMs: Long = System.currentTimeMillis(),
    ): Project {
        require(width > 0 && height > 0)
        require(fps in setOf(24f, 30f, 48f, 60f, 120f))
        val safeName = name.trim().ifEmpty { "King Motion Project %02d".format(projectNumber) }
        return Project(
            name = safeName,
            createdAtEpochMs = nowMs,
            updatedAtEpochMs = nowMs,
            settings = settings,
            compositions = listOf(Composition(name = safeName, width = width, height = height, frameRate = fps, durationUs = 10_000_000, layers = emptyList())),
        )
    }
}
