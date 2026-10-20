# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

All commands use the Gradle wrapper. On Windows, use `gradlew.bat`; on Unix, use `./gradlew`.

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run a single test class
./gradlew test --tests "ar.edu.unlam.mobile.scaffolding.ExampleUnitTest"

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Lint (ktlint)
./gradlew ktlintCheck

# Auto-fix lint
./gradlew ktlintFormat

# Code coverage report (Kover)
./gradlew koverHtmlReport
```

## Architecture

Single-module Android app (`ar.edu.unlam.mobile.scaffolding`) using Jetpack Compose + MVVM + Hilt.

### Navigation

`MainActivity` owns the `NavHost` and a shared `SnackbarHostState`. Routes are string constants defined in their respective screen files (`HOME_SCREEN_ROUTE`, `FORM_ROUTE`). The `user/{id}` route passes a `String` navArgument.

### ViewModel → Screen contract

Each screen has a dedicated ViewModel and a sealed `UIState` hierarchy with three variants:
- `Loading` — initial/pending state
- `Success(data)` — holds the result
- `Error(message)` — error payload surfaced via `onError: (Exception) -> Unit` callback on the screen

State is exposed as `StateFlow<XxxUIState>` and collected with `collectAsState()`. ViewModels are injected with `hiltViewModel()`.

### Error surface pattern

Screens do **not** own a Snackbar. Instead, they accept `onError: (Exception) -> Unit` and call it on the `Error` branch. `MainActivity` holds the `SnackbarHostState` and launches coroutines to show `SnackbarVisualsWithError`, which carries an `isError: Boolean` flag to switch between error and success styling.

### Dependency injection

`ScaffoldingApplication` is annotated `@HiltAndroidApp`. `MainActivity` is `@AndroidEntryPoint`. ViewModels use `@HiltViewModel` + `@Inject constructor()`.

### Key stack versions

| Tool | Version |
|---|---|
| AGP | 9.2.0 |
| Kotlin | 2.3.20 |
| Compose BOM | 2026.03.01 |
| Dagger/Hilt | 2.59.2 |
| compileSdk / targetSdk | 36 |
| minSdk | 24 |
| JVM target | 17 |

### Code style

ktlint is enforced via the `org.jlleitschuh.gradle.ktlint` plugin. Run `ktlintFormat` before committing. Kotlin language version is 2.1.
