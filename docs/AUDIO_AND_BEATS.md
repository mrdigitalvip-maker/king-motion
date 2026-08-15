# Audio and beats

Audio layers preserve a content URI, range, source offset, volume, and mute. Media3 plays a selected audio layer. **Extract Audio** creates an aligned non-destructive audio view of the video container; nothing is duplicated or uploaded.

`BeatDetector` is a local deterministic energy-onset algorithm. It calculates window energy, compares adaptive local/global thresholds, finds peaks, enforces a 200 ms refractory interval, and returns `BeatMarker(timestampUs, strength, sourceLayerId)`. It is suitable for a cancellable background coroutine.

The compressed-audio PCM decoder adapter is not implemented. Detect Beats therefore explains that analysis is unavailable; it never fabricates markers. Manual Add Beat works and markers render magenta. Previous/next, removal/clear, reanalysis, waveform, and Split on Beats remain planned.
