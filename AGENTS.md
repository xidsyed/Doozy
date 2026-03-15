# Doozy Project

Doozy is an Android app in active development. The current codebase already includes
onboarding, auth, todos, profile, and subscription/checkout flows built with Jetpack Compose.

The project is still mock-first in several areas: some repositories simulate backend behavior
with local state and delays while the real backend integrations are phased in.

## Current Integration Status
- Auth: Firebase Phone Auth is integrated and used in app auth flows.
- User/Subscription/Payment: mostly mocked or simulated in repositories for now.
- Local-first pattern: sync-based resource repositories are used to keep local state authoritative while syncing remote updates in the background where the repository exposes state modeled as a combination of `data?` and `syncStatus` with last successful sync timestamp for `Idle` state.
- Convex: dependency is present, but `convex/schema.ts` is currently empty.
- Analytics: planned, not yet implemented in the Android app layer.

## Android Stack (Current)
- Kotlin 2.3.x (currently 2.3.20-Beta1 in Gradle)
- Jetpack Compose + Material3
- Navigation 3
- Koin (DI)
- DataStore
- Room
- Paging 3
- Ktor
- WorkManager
- Firebase Auth
- Convex Android SDK (early-stage wiring)

## Rules and Guidance
Use this file for high-level project context and expectations.

Use `.agents/rules/*.md` for implementation-level conventions (screen patterns, DI, error handling,
validation, placeholder assets, build workflow, temporary file handling, and Stitch MCP usage notes).
