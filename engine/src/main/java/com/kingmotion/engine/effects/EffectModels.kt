package com.kingmotion.engine.effects

import com.kingmotion.engine.keyframes.AnimatedFloat

enum class ParameterType { FLOAT, COLOR, BOOLEAN }

data class EffectParameterDefinition(
    val id: String,
    val displayName: String,
    val type: ParameterType,
    val defaultValue: Float,
    val minimum: Float? = null,
    val maximum: Float? = null,
    val animatable: Boolean = true,
)

data class EffectDefinition(
    val schemaVersion: Int,
    val id: String,
    val name: String,
    val category: String,
    val fragmentShader: String,
    val parameters: List<EffectParameterDefinition>,
)

data class EffectParameter(
    val definitionId: String,
    val value: AnimatedFloat,
)

data class EffectInstance(
    val definitionId: String,
    val enabled: Boolean = true,
    val parameters: List<EffectParameter>,
)
