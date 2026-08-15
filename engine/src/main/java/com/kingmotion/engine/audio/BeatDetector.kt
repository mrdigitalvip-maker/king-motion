package com.kingmotion.engine.audio

import com.kingmotion.engine.model.BeatMarker

/** Deterministic, local energy-onset detector. PCM decoding is supplied by the Android adapter. */
object BeatDetector {
    fun detect(samples: FloatArray, sampleRate: Int, sourceLayerId: String? = null, windowSize: Int = 1024): List<BeatMarker> {
        require(sampleRate > 0 && windowSize > 0)
        if (samples.size < windowSize * 2) return emptyList()
        val energy = samples.asList().chunked(windowSize).map { frame -> frame.sumOf { (it * it).toDouble() }.toFloat() / frame.size }
        val average = energy.average().toFloat()
        val minimumGapFrames = maxOf(1, sampleRate / windowSize / 5)
        val peaks = mutableListOf<BeatMarker>()
        var last = -minimumGapFrames
        for (index in 1 until energy.lastIndex) {
            val localMean = energy.subList(maxOf(0, index - 8), index).average().toFloat()
            val threshold = maxOf(average * 1.35f, localMean * 1.6f)
            if (energy[index] > threshold && energy[index] >= energy[index - 1] && energy[index] > energy[index + 1] && index - last >= minimumGapFrames) {
                peaks += BeatMarker(timestampUs = index.toLong() * windowSize * 1_000_000L / sampleRate, strength = (energy[index] / (threshold + 1e-9f)).coerceIn(0f, 4f) / 4f, sourceLayerId = sourceLayerId)
                last = index
            }
        }
        return peaks
    }
}
