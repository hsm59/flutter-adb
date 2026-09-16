# Flutter ADB

An Android Studio plugin that runs common ADB app commands (uninstall, kill, start, restart, clear data, grant and revoke permissions) on the app of a Flutter project, without needing a synced Android module.

## Why not ADB Idea?

[ADB Idea](https://plugins.jetbrains.com/plugin/7380-adb-idea) finds your app's package name through the Android facet of a Gradle-synced Android module. When you open a Flutter project from its root folder, Android Studio sets it up as a Flutter/Dart project. The `android/` folder isn't imported as an Android module, so there is no Android facet. ADB Idea can't work out which app to target, and its actions don't work unless you open `android/` as a separate project.

Flutter ADB doesn't use the facet. It reads the package name straight from the files in `android/` and talks to devices through Android Studio's own ADB connection.

## Actions

All actions are under **Tools → Flutter ADB**.

| Action | What it does |
| --- | --- |
| Uninstall App | Uninstalls the app. |
| Kill App | Force-stops the app (`am force-stop`). |
| Start App | Launches the app's launcher activity. |
| Restart App | Force-stops the app, then launches it. |
| Clear App Data | Clears the app's data and cache (`pm clear`). |
| Clear App Data and Restart | Clears the data, then launches the app. |
| Grant Permissions | Grants every permission the app requests. |
| Revoke Permissions | Revokes every permission the app requests. |
| Set Package Name… | Overrides the auto-detected package name for this project. |

### Shortcut

**Ctrl+Shift+Alt+D** opens a numbered popup with all the actions, so you can press a digit to pick one. In the default macOS keymap, Ctrl becomes Cmd, so it's **⌘⇧⌥D**. To change it, go to **Settings → Keymap** and search for "Flutter ADB Operations Popup".

### How it works

- **Package name.** The plugin reads `android/app/build.gradle.kts` (or `build.gradle`) and takes the `applicationId`. If there isn't one, it uses `namespace`, and after that the `package` attribute in `android/app/src/main/AndroidManifest.xml`. Anything you set with **Set Package Name…** takes priority. That override is saved in `.idea/workspace.xml`, so it stays on your machine.
- **adb.** The plugin uses the Android SDK set in Android Studio. If none is set, it falls back to `sdk.dir` in `android/local.properties`, then to `ANDROID_HOME` or `ANDROID_SDK_ROOT`.
- **Devices.** With one device connected, the action runs straight away. With more than one, you get a list where you can pick a device or **All devices**. A notification shows the result for each device.

### Limitations

- **Flavors.** Detection takes the first `applicationId` it finds and ignores `applicationIdSuffix`. If you build with flavors, use **Set Package Name…** to choose the app.
- **Permissions.** Only runtime permissions can be granted or revoked. Install-time permissions are skipped, so the result can read something like `3/7 granted`.

## Install

The plugin isn't on JetBrains Marketplace yet, so you install it from a zip file.

1. Build the zip (see [Build](#build)).
2. In Android Studio, go to **Settings → Plugins**, click **⚙**, and choose **Install Plugin from Disk…**.
3. Select `flutter-adb-<version>.zip` and restart the IDE.

You need Android Studio 2026.1 (build 261) or newer.

## Build

You need JDK 21. The Gradle wrapper is included, so you don't need to install Gradle.

```sh
./gradlew buildPlugin   # Windows: gradlew.bat buildPlugin
```

The plugin zip is written to `build/distributions/flutter-adb-<version>.zip`.

To try the plugin in a sandboxed Android Studio:

```sh
./gradlew runIde
```

Gradle downloads the Android Studio release set by `studioVersion` in `gradle.properties` the first time you build. It's a large download, but it's cached for later builds. To compile against a different release, change that version.