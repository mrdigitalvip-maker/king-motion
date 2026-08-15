# King Motion

King Motion is an Android-first video and motion-design editor for creators who need precise control over layers, effects, animation, and export. The project is building an original, local-first editing engine with no mandatory backend or paid service.

## Vision

The long-term editing flow is **create project → import media → preview → arrange the timeline → apply effects → animate parameters with keyframes → export**. The current repository deliberately implements only the small, maintainable foundation for that flow.

## Stack

- Kotlin and Gradle Kotlin DSL
- Jetpack Compose with a custom dark Material 3 theme
- Modular `app` and `engine` boundaries
- OpenGL ES 2.0 shader foundation
- Android media APIs planned for decode, encode, and muxing
- Local processing and project storage by default

All current runtime dependencies are free, open-source Android/Jetpack components and require no hosted service.

## Local setup

The repository intentionally does not commit the Gradle Wrapper JAR because some
code-review and pull-request environments reject binary files. Install Gradle
8.10.2, then generate the wrapper locally before the first build:

```bash
gradle wrapper --gradle-version 8.10.2
./gradlew assembleDebug
```

On Windows, run the generated `gradlew.bat assembleDebug` command instead. The
generated wrapper files must not be added to a pull request in environments that
do not accept binary changes.

## Current status

The app contains a functional home-to-editor navigation flow, a three-region editor shell, core project/timeline models, linear float keyframes, a data-described effect contract, and an original brightness shader proof of concept. Media import, live preview rendering, persistence, audio processing, and export are not connected yet.

## Modules

- `app`: Compose UI, navigation, Android resources, and bundled effect assets.
- `engine`: editor-domain models, keyframe evaluation, effect definitions, and GPU program primitives.
- `docs`: architecture and technical decisions.

## Roadmap

1. Project persistence and Android photo-picker media import.
2. MediaExtractor/MediaCodec decode feeding a GPU preview surface.
3. Interactive multi-track timeline and parameter controls.
4. Effect graph execution and richer interpolation/easing.
5. Offline MediaCodec/MediaMuxer export, audio mixing, and reliability testing.

See [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) for the system design.
