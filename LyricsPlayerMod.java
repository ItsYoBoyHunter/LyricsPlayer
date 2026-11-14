package dev.lyricsplayer;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LyricsPlayerMod implements ModInitializer {
    public static final String MOD_ID = "lyricsplayer";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Lyrics Player prototype loaded. Audio/UI integration is the next stage.");
    }
}
