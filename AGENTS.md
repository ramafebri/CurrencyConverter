# AGENTS.md

Guidance for AI coding agents working in this repository.

## App Overview

**ArqFinance** (application id `com.arq.currencyconverter`) is a single-module Android app built with Kotlin and Jetpack Compose. It provides local (on-device) authentication, a portfolio-style home dashboard, a user profile screen, and its main feature: a live **Exchange Calculator** that converts between USDc and foreign currencies using real-time bid/ask rates from the [DolarApp API](https://api.dolarapp.dev/v1/).

The app starts at **Sign In** (or **Sign Up** for new users). Accounts are stored locally in Room with hashed passwords; session state persists via DataStore Preferences and is observed app-wide to redirect users back to Sign In when their session expires. After authentication, users land on **Home**, which shows sample portfolio balances and an entry point ("Start New Conversion") into the Exchange Calculator. The **Profile** screen shows basic user info.

The Exchange Calculator (`ConverterScreen` + `ConverterViewModel`) polls `GET tickers?currencies={code}` every minute for bid/ask rates and `GET tickers-currencies` for the list of supported currencies (MXN, ARS, BRL, COP, etc.). USDc is fixed on one side of the conversion; the user picks the foreign currency via a bottom sheet. Typing an amount in either field recalculates the other using the active rate (`bid` when USDc is on top, `ask` after swapping), with input sanitized through a currency formatter and calculations run off the main thread. Polling is lifecycle-aware — it pauses when the app backgrounds and resumes on foreground — and uses `flatMapLatest` so only the latest request stream drives the UI.

## Architecture

The codebase is organized by **feature**, each following a **data → domain → UI/ViewModel** layering, with shared utilities under `core/`:

```
app/src/main/java/com/arq/currencyconverter/
├── core/           # Shared network, formatting, validation, Room, DataStore
├── di/             # App-wide Hilt modules
├── navigator/      # Navigation 3 back stack, session-aware routing
└── feature/
    ├── converter/  # Exchange calculator (main feature)
    ├── home/
    ├── profile/
    ├── signin/
    └── signup/
```

- **Hilt** provides dependency injection, with per-feature modules (`*Module.kt`).
- **Navigation 3** (typed `NavKeys`) drives screen-to-screen flow via `AppNavigation`.
- A `SessionObserver`/`SessionViewModel` watches session validity and redirects to Sign In on expiry.
- An architecture guard test (`ArchitectureTest`) enforces **no cross-feature imports** — keep feature packages isolated from one another; only depend on `core`.

## Tech Stack

| Category | Technology |
|----------|------------|
| Language | Kotlin 2.4.0, JVM 17 |
| UI | Jetpack Compose + Material 3 |
| DI | Hilt + KSP |
| Navigation | Navigation 3 |
| Networking | Retrofit, OkHttp, Gson, kotlinx-serialization |
| Async | Kotlin Coroutines + Flow |
| Local storage | Room, DataStore Preferences |
| Static analysis | Detekt (+ ktlint rules), config at `config/detekt.yml` |
| Unit test coverage | Kover |
| Unit testing | JUnit 4, MockK, Turbine, coroutines-test |
| UI testing | Compose UI Test, Espresso, AndroidX JUnit |
| Debug tooling | LeakCanary, Chucker (debug builds only) |

`minSdk` 24 · `compileSdk` 37 · `targetSdk` 36. Dependency versions are centralized in `gradle/libs.versions.toml`.

## Build, Test & Lint Commands

Run from the repo root (Windows: `gradlew.bat`, macOS/Linux: `./gradlew`):

```bash
# Build debug APK
gradlew.bat assembleDebug

# Install debug APK on a connected device/emulator
gradlew.bat installDebug

# Run unit tests (JVM, no device required)
gradlew.bat testDebugUnitTest

# Run instrumented/UI tests (requires emulator or device)
gradlew.bat connectedDebugAndroidTest

# Static analysis (Detekt + ktlint rules)
gradlew.bat detekt

# Coverage report (after running unit tests)
gradlew.bat koverHtmlReportDebug

# Run all verification tasks
gradlew.bat check
```

Always run `testDebugUnitTest` and `detekt` (and `connectedDebugAndroidTest` when UI code changes) before considering a change complete.

## Conventions for Agents

- Preserve the **feature isolation** rule: do not import one feature's package from another; shared code belongs in `core/`.
- Follow the existing **data → domain → UI/ViewModel** layering when adding to a feature (repository interface in `domain`, implementation in `data`, state/logic in a ViewModel, screen in `ui`).
- Add unit tests under `app/src/test/` mirroring the source package path; add Compose UI tests under `app/src/androidTest/` for new screens.
- Keep Kover's exclusion filters in mind (`app/build.gradle.kts`) — generated code, Hilt modules, Room implementations, and `@Composable` functions are excluded from coverage targets by design.
- New dependencies/versions should be added to `gradle/libs.versions.toml`, not hardcoded in module `build.gradle.kts` files.
- See `README.md` for full setup, run, and testing instructions, and a detailed walkthrough of the Exchange Calculator's data flow.
