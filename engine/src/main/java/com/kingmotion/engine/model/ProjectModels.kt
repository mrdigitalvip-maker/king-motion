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
)

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
