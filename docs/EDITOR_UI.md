# Editor Studio UI

## Project creation flow
Home exposes common format defaults without requiring authentication. New Project collects type, canvas, resolution, aspect ratio, frame rate, quality and background. `ProjectFactory` normalizes empty names, creates the main composition, and transfers the selected format into the domain object before navigation.

## Editor Studio and state
`EditorStateHolder` is the UI-facing state boundary. Immutable `EditorState`, `TimelineState`, and `PlaybackState` snapshots are changed through `EditorAction`; composables only render state and emit actions. This makes undo persistence and a lifecycle ViewModel straightforward follow-ups. The compact phone layout separates top bar, preview, timeline, contextual actions, tools, and modal panels; larger form factors can place those panels side by side.

## Timeline UI
The timeline models selection, playhead and bounded `TimelineZoom` semantics, renders keyed layer rows, and supports video, image, audio, text, shape, solid, adjustment and nested compositions. Horizontal/pinch scaling and virtualized time tiles can later share the same zoom value. Layer controls reserve visibility, lock and audio mute concepts.

## Preview architecture
The current preview is a canvas surface placeholder. The intended renderer owns a dedicated GPU surface and render thread, consumes immutable composition snapshots, and never blocks Compose. Auto, quarter, half and full preview proxies are distinct from maximum-quality export rendering.

## Assets
Media browsing is organized around Videos, Images, Audio, Files and Assets, while the asset catalog separates Effects, Presets, Transitions, Overlays, Shapes, Fonts, CC, Shaders and Packs. Providers will expose metadata and thumbnails lazily; internal and externally installed packs use the same interface. Imports will report media below the 1080p project minimum before ingestion.

## Planned audio extraction pipeline
`Extract Audio` will validate a selected video layer, open its media source through a platform extractor, select the audio track, decode or stream-copy into project-managed storage, generate asynchronous waveform peaks, and insert a linked audio layer at the video timing offset. Progress, cancellation and errors will be actions in editor state. Decoding, waveform generation and file I/O run off the main thread; none is implemented in this visual phase.
