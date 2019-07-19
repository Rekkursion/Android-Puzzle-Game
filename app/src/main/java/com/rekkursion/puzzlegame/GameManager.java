package com.rekkursion.puzzlegame;

import android.graphics.Bitmap;

public class GameManager {
    public enum Direction {
        UP, RIGHT, DOWN, LEFT
    }

    private static final GameManager instance = new GameManager();

    public Bitmap originalImageBitmap;
    public Bitmap scaledImageBitmap;
    public int difficulty;
    public GamingModeEnum gamingMode;
    public int[][] tagNumbersMap;

    public void shuffle() {
        tagNumbersMap = new int[difficulty][difficulty];
        for (int r = 0; r < difficulty; ++r) {
            for (int c = 0; c < difficulty; ++c)
                tagNumbersMap[r][c] = r * difficulty + c;
        }

        for (int r = 0; r < difficulty; ++r) {
            for (int c = 0; c < difficulty; ++c) {
                int _r = (int) Math.floor(Math.random() * difficulty);
                int _c = (int) Math.floor(Math.random() * difficulty);

                if (_r >= difficulty) _r = difficulty - 1;
                if (_c >= difficulty) _c = difficulty - 1;

                int tmp = tagNumbersMap[r][c];
                tagNumbersMap[r][c] = tagNumbersMap[_r][_c];
                tagNumbersMap[_r][_c] = tmp;
            }
        }
    }

    public boolean isAbandonedTagNumber(int tag) {
        return tag == difficulty * difficulty - 1;
    }

    public int getAbandonedIndex() {
        return difficulty * difficulty - 1;
    }

    public int getTagAroundTheCertainTag(int certainTag, Direction dir) {
        for (int r = 0; r < difficulty; ++r) {
            for (int c = 0; c < difficulty; ++c) {
                if (tagNumbersMap[r][c] == certainTag) {
                    switch (dir) {
                        case UP: return r == 0 ? -1 : tagNumbersMap[r - 1][c];
                        case RIGHT: return c == difficulty - 1 ? -1 : tagNumbersMap[r][c + 1];
                        case DOWN: return r == difficulty - 1 ? -1 : tagNumbersMap[r + 1][c];
                        case LEFT: return c == 0 ? -1 : tagNumbersMap[r][c - 1];
                    }
                    return -1;
                }
            }
        }
        return -1;
    }

    public static GameManager getInstance() { return instance; }

    private GameManager() {}
}
