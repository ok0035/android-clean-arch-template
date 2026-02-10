# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
./gradlew assembleDebug              # Debug build
./gradlew assembleRelease            # Release build
./gradlew :app:installDebug          # Install debug APK to connected device

./gradlew test                       # All unit tests
./gradlew :data:test                 # Data module tests only
./gradlew :feature-main:test         # Feature module tests only
./gradlew connectedAndroidTest       # Instrumentation tests (requires device/emulator)
```

## Architecture

Multi-module Android project following **Clean Architecture** with unidirectional data flow:

```
UI (Compose) → ViewModel → Domain (Repository Interface) → Data (RepositoryImpl → Retrofit Service) → Remote API
```

### Modules

- **app** — Application entry point. `HiltApp` (`@HiltAndroidApp`), `MainActivity`, global DI (`AppModule`). Depends on all other modules.
- **domain** — Pure business contracts. Repository interfaces only, zero external dependencies. Package: `com.zerosword.domain`
- **data** — Network layer and repository implementations. Contains Retrofit services, Sandwich API response handling, Hilt DI modules (`NetworkModule`, `ApiModule`, `RepositoryModule`). Depends on domain only. Base URL configured via `buildConfigField` in `data/build.gradle.kts`.
- **feature-main** — Feature/screen module. Compose UI + `@HiltViewModel` with `StateFlow` state management. Depends on domain, data, resources.
- **resources** — Shared design system. Material 3 theme (Color, Type, Theme). No dependencies on other modules.

### Module Dependency Graph

```
app → feature-main, data, domain, resources
feature-main → data, domain, resources
data → domain
domain → (none)
resources → (none)
```

### Key Patterns

- **DI:** Hilt with per-module `@Module` classes. Repositories bound via `@Binds` in `RepositoryModule`.
- **Networking:** Retrofit 3 + OkHttp 5 (BOM) + Sandwich (`ApiResponse` wrapper with `suspendOnSuccess`/`suspendOnFailure`). Logging level varies by build type (BODY for debug, BASIC for release) — configured in `NetworkModule.kt`.
- **State:** ViewModels expose `StateFlow`; Compose UI collects via `collectAsState()`.
- **Theming:** Material 3 with dynamic color support (Android 12+), fallback to design system colors from resources module.

## Build Configuration

- **Version catalog:** `gradle/libs.versions.toml` — all dependency versions managed here
- **Kotlin:** 2.2.20, JVM target 17, Kapt for Hilt annotation processing
- **Compose:** BOM 2025.10.00, compiler plugin via `org.jetbrains.kotlin.plugin.compose`
- **Min SDK:** 24, Compile SDK: 36, Target SDK: 34

## Adding a New Feature Module

1. Create module directory with `build.gradle.kts` (use `feature-main` as template)
2. Add to `settings.gradle.kts`
3. Depend on `domain` for business interfaces, `resources` for theming
4. Create `@HiltViewModel` + Compose UI
5. Add module dependency in `app/build.gradle.kts`
