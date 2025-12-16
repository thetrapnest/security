# Приложение для тренировки навыков безопасности

Это Android‑приложение для отработки сценариев по безопасности. Ниже — подсказки, как посмотреть последние изменения в Git и как собрать/запустить приложение локально.

## Просмотр последних изменений
- Краткая история коммитов: `git log --oneline --decorate -5`
- Файлы последнего коммита: `git show HEAD`
- Состояние рабочего дерева: `git status -sb`
- Сравнение локальной ветки с удалённой (если настроена): `git diff origin/work...work`

## Запуск приложения
1. **Установите зависимости:** Android Studio (Hedgehog или новее) с Android SDK 34+, плагином Kotlin и эмулятором либо физическим устройством с включённой отладкой по USB.
2. **Синхронизируйте проект:** откройте репозиторий в Android Studio и дождитесь окончания синхронизации Gradle.
3. **Запуск из IDE:** выберите конфигурацию `app` и нажмите **Run**, чтобы установить приложение на подключённое устройство/эмулятор.
4. **Сборка из командной строки:** из корня репозитория выполните `./gradlew :app:assembleDebug`, после чего debug‑APK появится в `app/build/outputs/apk/debug/`.
5. **Очистка и сборка (опционально):** `./gradlew clean assembleDebug`

Если только что обновили зависимости или подтянули свежие изменения, выполните `./gradlew --refresh-dependencies`, чтобы убедиться, что плагины и библиотеки актуальны.
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
