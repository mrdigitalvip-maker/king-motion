package com.kingmotion.engine.effects

import org.json.JSONObject

object EffectDefinitionParser {
    fun parse(json: String): EffectDefinition {
        val root = JSONObject(json)
        val parameters = root.getJSONArray("parameters")
        return EffectDefinition(
            schemaVersion = root.getInt("schemaVersion"),
            id = root.getString("id"),
            name = root.getString("name"),
            category = root.getString("category"),
            fragmentShader = root.getString("fragmentShader"),
            parameters = List(parameters.length()) { index ->
                val item = parameters.getJSONObject(index)
                EffectParameterDefinition(
                    id = item.getString("id"),
                    displayName = item.getString("displayName"),
                    type = ParameterType.valueOf(item.getString("type").uppercase()),
                    defaultValue = item.getDouble("default").toFloat(),
                    minimum = item.optDouble("min").takeUnless(Double::isNaN)?.toFloat(),
                    maximum = item.optDouble("max").takeUnless(Double::isNaN)?.toFloat(),
                    animatable = item.optBoolean("animatable", true),
                )
            },
        )
    }
}
