package com.kingmotion.engine.keyframes

enum class Interpolation { LINEAR }

data class Keyframe(
    val timestampUs: Long,
    val value: Float,
    val interpolation: Interpolation = Interpolation.LINEAR,
)

data class AnimatedFloat(
    val initialValue: Float,
    val keyframes: List<Keyframe> = emptyList(),
) {
    fun valueAt(timestampUs: Long): Float {
        val ordered = keyframes.sortedBy(Keyframe::timestampUs)
        if (ordered.isEmpty()) return initialValue
        if (timestampUs < ordered.first().timestampUs) return initialValue
        val nextIndex = ordered.indexOfFirst { it.timestampUs > timestampUs }
        if (nextIndex == -1) return ordered.last().value
        val start = ordered[nextIndex - 1]
        val end = ordered[nextIndex]
        val progress = (timestampUs - start.timestampUs).toFloat() / (end.timestampUs - start.timestampUs)
        return start.value + (end.value - start.value) * progress
    }
}
