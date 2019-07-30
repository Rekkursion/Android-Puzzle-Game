package com.rekkursion.puzzlegame;

public class BackgroundMusicManager {
    public boolean shouldStopPlayingWhenLeaving;
    public String currentPlayingFilename;

    private static final BackgroundMusicManager instance = new BackgroundMusicManager();

    public static BackgroundMusicManager getInstance() {
        return instance;
    }

    private BackgroundMusicManager() {
        shouldStopPlayingWhenLeaving = true;
        // currentPlayingFilename = "gaming_theme" + File.separator + "game_maoudamashii_gaming_theme_1.mp3";
        currentPlayingFilename = "game_maoudamashii_main_theme.mp3";
    }
}
