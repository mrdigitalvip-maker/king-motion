# King Motion architecture

## Principles

King Motion is local-first, modular, and intentionally avoids a required backend. UI, editor-domain state, media I/O, and rendering should remain replaceable boundaries. The first milestone uses Kotlin throughout; C++/NDK is reserved for measured bottlenecks rather than being a default dependency.

## Module map

`app` owns Android lifecycle, Compose screens, navigation, and packaged resources. Its feature packages (`home`, `editor`, and later `projects`, `timeline`, `preview`, and `export`) should depend on engine APIs, never the other way around.

`engine` owns platform-independent domain concepts where practical. Current packages separate `model`, `keyframes`, `effects`, and `renderer`. Planned packages are `media`, `audio`, and `export`. A future large feature can become its own Gradle module without changing the persisted concepts.

## Project, composition, and layers

A `Project` groups one or more compositions. A `Composition` defines canvas dimensions, frame rate, duration, and an ordered layer list. Every `Layer` exposes timeline start, duration, order, opacity, and zero or more effect instances. `MediaLayer` additionally refers to a content URI and source trim position.

Times are integer microseconds to align with Android media APIs and avoid accumulated floating-point timing error. The UI may display frames or seconds, but conversions happen at its boundary. At a playhead time, active layers are filtered by their time range and sorted by `order` before compositing.

## Keyframes

An animatable float has an initial value and timestamped keyframes. Evaluation holds the initial value until the first keyframe, linearly interpolates between surrounding keyframes, and holds the final value afterwards. `Interpolation` is an enum so hold, easing, cubic Bézier handles, and a graph editor can be added without changing layer ownership.

Keyframes are parameter data, not UI state. This lets preview and export evaluate the exact same values.

## Effects and external schema

An effect has two parts:

1. A JSON definition in `app/src/main/assets/effects`, parsed into an `EffectDefinition`.
2. A shader in `app/src/main/assets/shaders`, referenced by the definition.

Schema version 1 requires:

| Field | Meaning |
| --- | --- |
| `schemaVersion` | Integer contract version; currently `1`. |
| `id` | Stable, globally unique reverse-domain identifier. |
| `name` | Human-readable display name. |
| `category` | Library grouping such as `color` or `distortion`. |
| `fragmentShader` | Asset-relative fragment shader path. |
| `parameters` | Ordered control descriptions. |

Each parameter has a stable `id`, `displayName`, `type`, `default`, optional `min`/`max`, and `animatable`. Runtime `EffectInstance` values refer to definition and parameter IDs, so projects do not duplicate catalog metadata. Definitions must be validated before entering the catalog; unknown schema versions or missing shaders should be rejected with a user-safe diagnostic.

The bundled brightness effect is original and deliberately minimal. Its animated float parameter adds an offset to sampled RGB channels and clamps the result while preserving alpha. This proves the definition-to-uniform design without adopting third-party shaders.

## Render pipeline

The target preview pipeline is:

1. `MediaExtractor` reads local media tracks.
2. `MediaCodec` decodes video into a GPU-backed surface.
3. The renderer selects active layers at the playhead and resolves animated properties.
4. Each enabled effect executes as an ordered framebuffer pass; schema parameter IDs map to shader uniforms.
5. Layers are transformed and alpha-composited in timeline order to the preview surface.

`GlShaderProgram` currently supplies shader compilation, linking, uniform assignment, and deterministic cleanup. Surface management, textures, framebuffer pooling, and render scheduling remain future work. Export will reuse the same render graph with an encoder input surface, then combine video and audio using `MediaMuxer`; this prevents preview/export visual divergence.

## UI and state

Compose renders immutable editor state and emits user intents. The current in-memory navigation is intentionally small. As persistence arrives, screen state will move to ViewModels and repositories; renderer lifetimes remain managed below Compose. UI code must not parse schemas or operate codecs directly.

## Technology decisions

- **Minimum Android 8 (API 26):** a practical baseline for modern codec and storage behavior.
- **Compose/Material 3:** declarative UI without a proprietary design dependency.
- **OpenGL ES 2.0 foundation:** broad device support for an initial shader pipeline. Capabilities can select newer GLES paths later.
- **Android media APIs:** device-local decode/encode with no recurring service cost.
- **No NDK yet:** profiling must justify native complexity.
- **No cloud dependency:** project data and assets remain usable offline.

## Extension path

New effects add a versioned definition and shader, then pass schema and render tests. New layer types implement the `Layer` contract and provide a renderer adapter. Audio can add track and automation models alongside layers while using the common microsecond clock. Persistence should introduce explicit versioned DTOs and migrations instead of serializing runtime classes directly. Feature modules may be split from `app` as their build times and ownership justify it.
