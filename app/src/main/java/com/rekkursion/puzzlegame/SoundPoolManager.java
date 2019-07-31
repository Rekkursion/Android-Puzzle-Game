package com.rekkursion.puzzlegame;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundPoolManager {
    private static final int MAX_STREAMS = 100;
    private static final float volume = 1.0F;
    public static final String SOUND_FILES_ROOT_PATH = "sounds";

    private SoundPool sp;
    private Map<String, Integer> audioFilenameAndSoundIdHashMap;
    private static SoundPoolManager instance;

    public void initSoundPool(Context context, List<String> audioFilenameList) throws IOException {
        if (audioFilenameList == null) return;

        if (sp != null)
            sp = null;
        sp = new SoundPool.Builder()
                .setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build()
                )
                .setMaxStreams(MAX_STREAMS)
                .build();
        sp.setOnLoadCompleteListener((soundPool, i, i1) -> {

        });

        if (audioFilenameAndSoundIdHashMap != null)
            audioFilenameAndSoundIdHashMap = null;
        audioFilenameAndSoundIdHashMap = new HashMap<>();

        int len = audioFilenameList.size();
        for (int k = 0; k < len; ++k) {
            String audioFilename = audioFilenameList.get(k);
            int streamId = sp.load(context.getAssets().openFd(SOUND_FILES_ROOT_PATH + File.separator + audioFilename), k);
            audioFilenameAndSoundIdHashMap.putIfAbsent(audioFilename, streamId);
        }
    }

    // return stream-id of the sound-pool
    public int play(String filename) throws NullPointerException {
        return this.play(filename, 0);
    }

    // return stream-id of the sound-pool
    public int play(String filename, int times) throws NullPointerException {
        return this.play(filename, times, 1.0F);
    }

    // return stream-id of the sound-pool
    public int play(String filename, int times, float rate) throws NullPointerException {
        return sp.play(getSoundIdByFilename(filename), volume, volume, 1, times, rate);
    }

    private int getSoundIdByFilename(String audioFilename) throws NullPointerException {
        return audioFilenameAndSoundIdHashMap.get(audioFilename);
    }

    public static SoundPoolManager getInstance() {
        if (instance == null)
            instance = new SoundPoolManager();
        return instance;
    }

    private SoundPoolManager() {}
}
