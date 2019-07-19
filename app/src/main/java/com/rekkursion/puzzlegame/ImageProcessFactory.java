package com.rekkursion.puzzlegame;

import android.graphics.Bitmap;

public class ImageProcessFactory {
    public static Bitmap scaleImage_fitCenter(Bitmap origBitmap, int fittingSize) {
        Bitmap scaledBitmap = Bitmap.createBitmap(fittingSize, fittingSize, Bitmap.Config.ARGB_8888);
        int imageWidth = origBitmap.getWidth();
        int imageHeight = origBitmap.getHeight();

        // step 1: fill in with black pixels
        int[] pixels = new int[fittingSize * fittingSize];
        for (int r = 0; r < fittingSize; ++r) {
            for (int c = 0; c < fittingSize; ++c) {
                int idx = r * fittingSize + c;
                pixels[idx] = 0xff000000;
            }
        }

        // step 2: scale the original bitmap (current method is fit-center)
        int startRowIdx = fittingSize == imageHeight ? 0 : (fittingSize - imageHeight) / 2;
        int startColIdx = fittingSize == imageWidth ? 0 : (fittingSize - imageWidth) / 2;
        for (int r = startRowIdx; r < startRowIdx + imageHeight; ++r) {
            for (int c = startColIdx; c < startColIdx + imageWidth; ++c) {
                int idx = r * fittingSize + c;
                pixels[idx] = origBitmap.getPixel(c - startColIdx, r - startRowIdx);
            }
        }

        scaledBitmap.setPixels(pixels, 0, fittingSize, 0, 0, fittingSize, fittingSize);
        return scaledBitmap;
    }

    public static Bitmap[][] divideImage(Bitmap origBitmap, int rowNum, int colNum) {
        Bitmap[][] splittedBitmapsArray = new Bitmap[rowNum][colNum];
        int singleSplittedBitmapWidth = origBitmap.getWidth() / colNum;
        int singleSplittedBitmapHeight = origBitmap.getHeight() / rowNum;

        for (int r = 0; r < rowNum; ++r) {
            for (int c = 0; c < colNum; ++c)
                splittedBitmapsArray[r][c] = Bitmap.createBitmap(origBitmap, c * singleSplittedBitmapWidth, r * singleSplittedBitmapHeight, singleSplittedBitmapWidth, singleSplittedBitmapHeight);
        }

        return splittedBitmapsArray;
    }
}
