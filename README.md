# King Motion

King Motion is a local-first Android video and motion editor. This milestone is an honest, installable editor prototype: project setup and reopening, Storage Access Framework media import, Media3 preview, a multi-layer timeline, non-destructive edits, transform/keyframe data, manual beat markers, autosave, and an artifact-only Android build.

## What works

- Create named/default projects at 1080p, 1440p, or 4K with aspect, 24–120 FPS, quality, type, and background metadata.
- Reopen locally saved projects; only content URIs and metadata are persisted.
- Import video, image, and audio with `ACTION_OPEN_DOCUMENT` and durable URI access. Videos below 1080p are rejected safely.
- Preview selected video/audio with Media3; play/pause, frame stepping, scrub, horizontal timeline scroll, zoom, selection, and multiple layers.
- Split, duplicate, delete, reorder, and trim domain operations; bounded undo/redo; opacity and opacity keyframe editing.
- Non-destructively expose a video's audio as an aligned audio layer and add/show beat markers.
- Add the bundled Brightness definition from Color (GPU execution is not connected).
- Debounced local autosave with Saving/Saved feedback.

## Explicit limitations

Automatic beat DSP exists and is deterministically tested in `engine`, but the compressed-audio-to-PCM adapter is incomplete, so **Detect Beats reports that it is unavailable** rather than generating fake results. Extract Audio creates a linked playable layer; it does not write a file. Preview-quality selection is state only. Image compositing, proxy rendering, full transform controls, keyframe navigation, effects execution, split-on-beats, capability fallback, and real export remain planned.

## Build

Install JDK 17, Android SDK 35, and Gradle 8.10.2, then run `gradle test` and `gradle assembleDebug`. The untracked APK is produced at `app/build/outputs/apk/debug/app-debug.apk`. GitHub Actions publishes it only as the `king-motion-debug` artifact. See [Android build instructions](docs/ANDROID_BUILD.md).

## Modules

- `app`: Compose UI, document/media adapters, JSON project store, Media3 player, and XML resources.
- `engine`: models, timeline/history, media policy, onset detection, effects, keyframes, and renderer primitives.
- `docs`: architecture, engine/audio design, build instructions, and submitted-asset analysis.
