# Security Training App

This repository contains an Android application for practicing security scenarios. The notes below show how to inspect the latest changes in Git and how to run the app locally.

## Viewing recent changes
- Show concise history with commits: `git log --oneline --decorate -5`
- Inspect files changed in the latest commit: `git show HEAD`
- Check working tree state: `git status -sb`
- Compare your local branch against the remote (if set): `git diff origin/work...work`

## Running the app
1. **Install dependencies:** Android Studio (Hedgehog or newer) with Android SDK 34+, Kotlin plugin, and an emulator or physical device with USB debugging.
2. **Sync the project:** Open the repository in Android Studio and let Gradle sync complete.
3. **Run from IDE:** Select the `app` configuration and click **Run** to install on the connected device/emulator.
4. **Command line build:** From the repo root run `./gradlew :app:assembleDebug` to produce a debug APK at `app/build/outputs/apk/debug/`.
5. **Clean build (optional):** `./gradlew clean assembleDebug`

If you pull fresh changes, run `./gradlew --refresh-dependencies` once to ensure plugins and libraries are up to date.
