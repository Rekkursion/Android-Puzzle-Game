package com.rekkursion.puzzlegame;

import android.graphics.Bitmap;
import android.util.Log;

public class GameManager {
    private static final GameManager instance = new GameManager();

    public Bitmap originalImageBitmap;
    public Bitmap scaledImageBitmap;
    public Bitmap[][] splittedBitmapsArray;
    public Bitmap lastSplittedBitmap;
    public int difficulty;
    public GamingModeEnum gamingMode;
    public int numOfSplittedBitmaps;
    private int idxOfAbandonedBitmap;

    public int getIdxOfAbandonedBitmap() {
        return idxOfAbandonedBitmap;
    }

    public Bitmap[][] abandonTheLastBitmapAndShuffle() {
        int splittedBitmapSize = splittedBitmapsArray[0][0].getWidth();
        int[] transparentPixels = new int[splittedBitmapSize * splittedBitmapSize];
        ImageProcessFactory.getSolidColorPixelsArray(transparentPixels, splittedBitmapSize, splittedBitmapSize, 0, 0, 0, 0);

        splittedBitmapsArray[difficulty - 1][difficulty - 1].setPixels(transparentPixels, 0, splittedBitmapSize, 0, 0, splittedBitmapSize, splittedBitmapSize);
        transparentPixels = null;

        idxOfAbandonedBitmap = numOfSplittedBitmaps - 1;

        // shuffle
        for (int k = 0; k < numOfSplittedBitmaps; ++k) {
            int j = (int) Math.floor(Math.random() * numOfSplittedBitmaps);
            if (j >= numOfSplittedBitmaps) j = numOfSplittedBitmaps - 1;

            int rowIdx_k = k / difficulty, colIdx_k = k % difficulty;
            int rowIdx_j = j / difficulty, colIdx_j = j % difficulty;

            if (k == idxOfAbandonedBitmap)
                idxOfAbandonedBitmap = j;
            else if (j == idxOfAbandonedBitmap)
                idxOfAbandonedBitmap = k;

            Bitmap tmp = splittedBitmapsArray[rowIdx_k][colIdx_k];
            splittedBitmapsArray[rowIdx_k][colIdx_k] = splittedBitmapsArray[rowIdx_j][colIdx_j];
            splittedBitmapsArray[rowIdx_j][colIdx_j] = tmp;
        }

        return splittedBitmapsArray;
    }

    public void exchangeBlocks(int idxOfClicked, int idxOfAbandoned) {
        idxOfAbandonedBitmap = idxOfClicked;
    }

    public static GameManager getInstance() { return instance; }

    private GameManager() {}
}
