# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Ice Fishing Riches is an Android puzzle/merge game built with Kotlin and Jetpack Compose. The app package is `beatmaker.edm.musicgames.PianoGa`.

## Build Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests (requires device/emulator)
./gradlew app:testDebugUnitTest  # Run unit tests for debug variant only
```

## Architecture

- **Single module** project (`:app`) using Gradle Kotlin DSL with a version catalog (`gradle/libs.versions.toml`)
- **UI**: Jetpack Compose with Material 3, edge-to-edge display
- **Entry point**: `LoadingActivity` is the launcher activity; `MainActivity` is navigated to after loading
- **Min SDK 28 / Target SDK 36**, compiled with Java 11 source compatibility

## Resources

- `res/raw/` contains audio assets: background music (`game_music.mp3`) and level result sounds (`level_win.mp3`, `level_lose.mp3`)
- `res/font/font.ttf` is a custom font
- `res/drawable/bg_1.png` is a background image asset
