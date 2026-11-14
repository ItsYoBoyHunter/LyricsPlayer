package dev.lyricsplayer;

import dev.lyricsplayer.library.Track;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class LyricsPlayerClient implements ClientModInitializer {
    private static KeyMapping openLibraryKey;

    @Override
    public void onInitializeClient() {
        openLibraryKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.lyricsplayer.open",
                GLFW.GLFW_KEY_O,
                KeyMapping.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openLibraryKey.consumeClick()) {
                if (client.player != null) {
                    client.setScreen(new LibraryScreen());
                }
            }
        });
    }

    static Path libraryRoot() {
        return Minecraft.getInstance().gameDirectory.toPath()
                .resolve("config").resolve("lyricsplayer");
    }

    static void ensureFolders() throws IOException {
        Path root = libraryRoot();
        Files.createDirectories(root.resolve("import"));
        Files.createDirectories(root.resolve("music"));
        Files.createDirectories(root.resolve("lyrics"));
    }

    static final class LibraryScreen extends Screen {
        private String status = "Put .ogg and .lrc files in the import folder, then tap Rescan.";
        private List<Track> tracks = List.of();

        protected LibraryScreen() {
            super(Component.literal("Lyrics Player"));
        }

        @Override
        protected void init() {
            int cx = width / 2;
            int top = height / 4;

            addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                    Component.literal("Rescan / Import songs"),
                    button -> scan()
            ).bounds(cx - 100, top, 200, 20).build());

            addRenderableWidget(net.minecraft.client.gui.components.Button.builder(
                    Component.literal("Close"),
                    button -> onClose()
            ).bounds(cx - 100, top + 100, 200, 20).build());

            scan();
        }

        private void scan() {
            try {
                ensureFolders();
                Path root = libraryRoot();
                Path importDir = root.resolve("import");
                Path musicDir = root.resolve("music");
                Path lyricsDir = root.resolve("lyrics");

                int imported = 0;
                try (var files = Files.list(importDir)) {
                    for (Path audio : files.filter(Files::isRegularFile).toList()) {
                        String name = audio.getFileName().toString();
                        if (!name.toLowerCase().endsWith(".ogg")) continue;

                        String base = name.substring(0, name.length() - 4);
                        Path lyric = importDir.resolve(base + ".lrc");
                        Files.copy(audio, musicDir.resolve(name),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        if (Files.isRegularFile(lyric)) {
                            Files.copy(lyric, lyricsDir.resolve(base + ".lrc"),
                                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        }
                        imported++;
                    }
                }

                tracks = dev.lyricsplayer.library.LibraryIndex.read(root);
                status = "Found " + imported + " audio file(s). Add matching .lrc files for synced lyrics.";
            } catch (IOException | RuntimeException exception) {
                status = "Library scan failed: " + exception.getMessage();
            }
        }

        @Override
        public void render(net.minecraft.client.gui.GuiGraphics graphics,
                           int mouseX, int mouseY, float delta) {
            renderBackground(graphics, mouseX, mouseY, delta);
            super.render(graphics, mouseX, mouseY, delta);
            graphics.drawCenteredString(font, title, width / 2, height / 4 - 25, 0xFFFFFF);
            graphics.drawCenteredString(font, status, width / 2, height / 4 + 32, 0xDDDDDD);
            graphics.drawCenteredString(font,
                    "Import folder: .minecraft/config/lyricsplayer/import",
                    width / 2, height / 4 + 49, 0xAAAAAA);
            graphics.drawCenteredString(font,
                    "Pair each song.ogg with song.lrc (same filename).",
                    width / 2, height / 4 + 64, 0xAAAAAA);
            graphics.drawCenteredString(font, "Library index entries: " + tracks.size(),
                    width / 2, height / 4 + 82, 0xFFFFFF);
        }

        @Override
        public boolean isPauseScreen() {
            return false;
        }
    }
}
