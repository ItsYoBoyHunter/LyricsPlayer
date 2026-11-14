# Lyrics Player — Fabric 26.2 prototype

This is a **source prototype**, not an installable `.jar` yet. It contains:
- Fabric 26.2 project metadata and build setup
- LRC timestamp parser and current-line lookup
- Expandable playlist index reader
- Android compatibility notes

## Target
- Minecraft Java Edition 26.2
- Fabric Loader 0.19.3
- Fabric API 0.161.0+26.2
- Java 25
- Zalith Launcher 2 / Android is a compatibility target, not yet verified
- MobileGlues is the user's selected renderer; renderer compatibility still needs testing

## Add tracks (planned folder layout)

The final in-game client implementation will read a library folder like:

```text
.minecraft/config/lyricsplayer/
  library.properties
  music/
    example.ogg
  lyrics/
    example.lrc
```

Example `library.properties`:

```properties
track.count=1
track.1.id=example
track.1.title=Example Track
track.1.audio=music/example.ogg
track.1.lyrics=lyrics/example.lrc
```

Example `example.lrc`:

```text
[00:04.20]First lyric line
[00:08.50]Second lyric line
[00:12.00]Third lyric line
```

Use audio you created or have permission to use. Prefer OGG Vorbis for Minecraft's audio pipeline.

## Important implementation status

This prototype does **not** yet include:
- an in-game player screen or keybind
- audio playback and seek controls
- dynamic scanning/import UI
- 3D lyric rendering
- a built `.jar`

Those pieces require client-side integration with the exact 26.2 Minecraft/Fabric APIs and testing on Android. Avoid using Java Sound, desktop media APIs, or Windows-only audio capture: they are not suitable assumptions for Android.

## Build environment

Fabric's 26.2 documentation specifies Java 25. Install JDK 25 and use a desktop Gradle environment to build:

```bash
./gradlew build
```

The resulting mod jar will be in `build/libs/` after a successful build. This source package does not include the Gradle wrapper JAR, so generate/add the wrapper from a Gradle 9.5.1 installation before building.

## References
- Fabric 26.2 setup: https://docs.fabricmc.net/develop/getting-started/setting-up
- Fabric 26.2 custom sounds: https://docs.fabricmc.net/develop/sounds/custom
- Fabric 26.2 build guide: https://docs.fabricmc.net/develop/getting-started/building-a-mod


## In-game import button (prototype)

Press **O** in-game to open the Lyrics Player screen, then tap **Rescan / Import songs**.
Copy audio files into:

`.minecraft/config/lyricsplayer/import/`

Supported import audio extension in this prototype: `.ogg`.
For synchronized lyrics, place a matching `.lrc` beside the audio file, e.g.
`track.ogg` + `track.lrc`. The button copies these into the library's `music/`
and `lyrics/` folders.

**Android note:** this uses Minecraft's game directory and a folder workflow rather
than Android's system document picker. On Zalith, use its file manager or Android
file manager to copy files into the instance's `config/lyricsplayer/import` folder.
Actual audio playback and lyric display are not implemented yet; this adds the
in-game library/import screen only.
