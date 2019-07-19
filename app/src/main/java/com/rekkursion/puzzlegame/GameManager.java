package com.rekkursion.puzzlegame;

import android.graphics.Bitmap;

public class GameManager {
    private static final GameManager instance = new GameManager();

    public Bitmap originalImageBitmap;
    public Bitmap scaledImageBitmap;
    public int difficulty;
    public GamingModeEnum gamingMode;
    private int[][] puzzleIndicesMap;

    public static GameManager getInstance() { return instance; }

    private GameManager() {}
}
