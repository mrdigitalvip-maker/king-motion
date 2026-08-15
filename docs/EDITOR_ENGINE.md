# Editor engine

Projects own settings and compositions; compositions own ordered layers and beat markers. Microseconds are canonical. Layers contain source URI/trim, timeline range, visibility/lock/mute, volume, transform channels, and effect instances. Changes are non-destructive.

`TimelineEditor` implements add/delete/duplicate/split/trim/reorder and normalizes order. `EditHistory` retains at most 50 immutable metadata states. Compose owns ephemeral selection, zoom, playhead, and preview quality.

The Media3 adapter previews a selected playable layer and seeks from the shared playhead. It is deliberately outside `engine`. This pass does not composite multiple visual layers, downscale proxies, render effects, or verify every high-FPS codec profile. Export honestly reports the absent encoder pipeline.

`ACTION_OPEN_DOCUMENT` filters MIME categories without broad storage permission and grants durable reads. Metadata is read on `Dispatchers.IO`; `VideoImportPolicy` requires 1920×1080 in either orientation.

Next adapters are a cancellable MediaExtractor/MediaCodec GPU scheduler, capability queries and reduced preview, renderer transform/effect evaluation, and encoder/audio-mixer/MediaMuxer export.
