# Android debug build and installation

Install JDK 17, Android SDK Platform 35/build tools, and Gradle 8.10.2. Run `gradle test assembleDebug`. The ignored APK appears under `app/build/outputs/apk/debug/`. Install with `adb install -r app/build/outputs/apk/debug/app-debug.apk`, or transfer it and follow Android's standard per-source installer prompt.

`.github/workflows/android-debug.yml` installs Temurin 17 and Gradle, tests, builds, and uploads the APK as `king-motion-debug`. On a phone, download/extract that run artifact and open the APK. This is private testing, not public distribution; no security control is bypassed.

The manifest requests no storage or network permission. The system picker grants document URIs. Only the launcher activity is exported. The icon is entirely XML.
