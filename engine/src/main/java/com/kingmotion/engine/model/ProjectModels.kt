package com.kingmotion.engine.model

import com.kingmotion.engine.effects.EffectInstance
import com.kingmotion.engine.keyframes.AnimatedFloat
import java.util.UUID

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long,
    val compositions: List<Composition>,
    val configuration: ProjectConfiguration = ProjectConfiguration(),
)

enum class ProjectType { VIDEO_EDIT, MOTION_COMPOSITION, EMPTY }
enum class ProjectQuality { STANDARD, HIGH, ULTRA, MAXIMUM }
enum class BackgroundType { BLACK, WHITE, TRANSPARENT, GRAY, CUSTOM }

data class ProjectConfiguration(
    val width: Int = 1920,
    val height: Int = 1080,
    val frameRate: Int = 30,
    val quality: ProjectQuality = ProjectQuality.HIGH,
    val background: BackgroundType = BackgroundType.BLACK,
    val customBackgroundHex: String = "#000000",
    val type: ProjectType = ProjectType.VIDEO_EDIT,
) {
    val aspectRatio: String get() = when {
        width * 9 == height * 16 -> "16:9"
        width * 16 == height * 9 -> "9:16"
        width == height -> "1:1"
        width * 5 == height * 4 -> "4:5"
        width * 4 == height * 3 -> "3:4"
        width * 3 == height * 4 -> "4:3"
        else -> "Custom"
    }
}

object ProjectFactory {
    fun create(name: String, configuration: ProjectConfiguration, index: Int = 1, now: Long = System.currentTimeMillis()): Project {
        val resolvedName = name.trim().ifEmpty { "King Motion Project ${index.toString().padStart(2, '0')}" }
        val composition = Composition(
            name = "Main Composition", width = configuration.width, height = configuration.height,
            frameRate = configuration.frameRate.toFloat(), durationUs = 10_000_000, layers = emptyList(),
        )
        return Project(name = resolvedName, createdAtEpochMs = now, updatedAtEpochMs = now,
            compositions = listOf(composition), configuration = configuration)
    }
}

data class Composition(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val width: Int,
    val height: Int,
    val frameRate: Float,
    val durationUs: Long,
    val layers: List<Layer>,
)

sealed interface Layer {
    val id: String
    val name: String
    val startTimeUs: Long
    val durationUs: Long
    val order: Int
    val opacity: AnimatedFloat
    val effects: List<EffectInstance>

    fun contains(timeUs: Long): Boolean = timeUs in startTimeUs until (startTimeUs + durationUs)
}

data class MediaLayer(
    override val id: String = UUID.randomUUID().toString(),
    override val name: String,
    override val startTimeUs: Long,
    override val durationUs: Long,
    override val order: Int,
    override val opacity: AnimatedFloat = AnimatedFloat(1f),
    override val effects: List<EffectInstance> = emptyList(),
    val sourceUri: String,
    val sourceInTimeUs: Long = 0,
) : Layer
