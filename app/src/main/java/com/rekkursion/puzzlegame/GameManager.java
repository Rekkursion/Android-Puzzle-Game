package com.rekkursion.puzzlegame;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

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
    private Map<Integer, Integer> tagAndIdDict;

    public void addTagAndIdEntry(int newTag, int newId) {
        if (tagAndIdDict == null)
            tagAndIdDict = new HashMap<>();
        tagAndIdDict.putIfAbsent(newTag, newId);
    }

    public void clearTagAndIdDict() {
        if (tagAndIdDict != null)
            tagAndIdDict.clear();
        tagAndIdDict = null;
    }

    public int getImageViewIdByTag(int tag) {
        return tagAndIdDict.getOrDefault(tag, -1);
    }

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

    public void swapImageViews(ImageView view_a, ImageView view_b) {
        int tag_a = (int) view_a.getTag(), tag_b = (int) view_b.getTag();
        int id_a = view_a.getId(), id_b = view_b.getId();
        Bitmap bitmap_a = ((BitmapDrawable) view_a.getDrawable()).getBitmap(), bitmap_b = ((BitmapDrawable) view_b.getDrawable()).getBitmap();

        view_a.setTag(tag_b); view_a.setId(id_b); view_a.setImageBitmap(bitmap_b);
        view_b.setTag(tag_a); view_b.setId(id_a); view_b.setImageBitmap(bitmap_a);

        int alpha_a = (int) Math.floor((double) view_a.getAlpha() * 255.0d);
        int alpha_b = (int) Math.floor((double) view_b.getAlpha() * 255.0d);

        if (alpha_a == 0) {
            view_a.setAlpha(1.0f);
            view_b.setAlpha(0.0f);
        } else if (alpha_b == 0) {
            view_a.setAlpha(0.0f);
            view_b.setAlpha(1.0f);
        }

        swapTagNumbers(tag_a, tag_b);
    }

    public boolean isAbandonedTagNumber(int tag) {
        return tag == difficulty * difficulty - 1;
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

    private void swapTagNumbers(int tag_a, int tag_b) {
        int _r = -1, _c = -1;
        for (int r = 0; r < difficulty; ++r) {
            for (int c = 0; c < difficulty; ++c) {
                if (tagNumbersMap[r][c] == tag_a || tagNumbersMap[r][c] == tag_b) {
                    if (_r < 0)
                    { _r = r; _c = c; }
                    else {
                        int tmp = tagNumbersMap[r][c];
                        tagNumbersMap[r][c] = tagNumbersMap[_r][_c];
                        tagNumbersMap[_r][_c] = tmp;
                        return;
                    }
                }
            }
        }
    }

    private GameManager() {}
}
