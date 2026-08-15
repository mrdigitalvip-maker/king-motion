package com.kingmotion.engine.media

data class MediaInfo(val width: Int, val height: Int, val durationUs: Long, val hasAudio: Boolean)
sealed interface MediaValidation { data object Accepted : MediaValidation; data class Rejected(val reason: String) : MediaValidation }

data class VideoImportPolicy(val minimumLongEdge: Int = 1920, val minimumShortEdge: Int = 1080) {
    fun validate(info: MediaInfo): MediaValidation {
        val long = maxOf(info.width, info.height); val short = minOf(info.width, info.height)
        return if (long >= minimumLongEdge && short >= minimumShortEdge) MediaValidation.Accepted
        else MediaValidation.Rejected("Video must be at least 1080p; selected media is ${info.width}×${info.height}.")
    }
}
