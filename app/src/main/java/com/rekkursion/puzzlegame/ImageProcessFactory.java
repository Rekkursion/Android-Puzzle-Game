package com.rekkursion.puzzlegame;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ImageProcessFactory {
    public static Bitmap scaleImage_fitCenter(Bitmap origBitmap) {
        int imageWidth = origBitmap.getWidth();
        int imageHeight = origBitmap.getHeight();
        int fittingSize = imageHeight > imageWidth ? imageHeight : imageWidth;
        Bitmap scaledBitmap = Bitmap.createBitmap(fittingSize, fittingSize, Bitmap.Config.ARGB_8888);

        // step 1: fill in with black pixels
        int[] pixels = new int[fittingSize * fittingSize];
        getSolidColorPixelsArray(pixels, fittingSize, fittingSize, 0, 0, 0, 255);

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

    public static Bitmap scaleImage_fitXY(Bitmap origBitmap) {
        int imageWidth = origBitmap.getWidth();
        int imageHeight = origBitmap.getHeight();
        int fittingSize = imageHeight > imageWidth ? imageHeight : imageWidth;
        double widthRatio = (double) imageWidth / (double) fittingSize;
        double heightRatio = (double) imageHeight / (double) fittingSize;
        Bitmap scaledBitmap = Bitmap.createBitmap(fittingSize, fittingSize, Bitmap.Config.ARGB_8888);

        int[] pixels = new int[fittingSize * fittingSize];
        for (int r = 0; r < fittingSize; ++r) {
            for (int c = 0; c < fittingSize; ++c) {
                int pixelIdx = r * fittingSize + c;
                int origImgRowIdx = (int) Math.floor(r * heightRatio);
                int origImgColIdx = (int) Math.floor(c * widthRatio);

                if (origImgRowIdx >= imageHeight) origImgRowIdx = imageHeight - 1;
                if (origImgColIdx >= imageWidth) origImgColIdx = imageWidth - 1;

                pixels[pixelIdx] = origBitmap.getPixel(origImgColIdx, origImgRowIdx);
            }
        }

        scaledBitmap.setPixels(pixels, 0, fittingSize, 0, 0, fittingSize, fittingSize);
        return scaledBitmap;
    }

    public static Bitmap scaleImage_fitStart(Bitmap origBitmap) {
        int imageWidth = origBitmap.getWidth();
        int imageHeight = origBitmap.getHeight();
        int fittingSize = imageHeight > imageWidth ? imageHeight : imageWidth;
        Bitmap scaledBitmap = Bitmap.createBitmap(fittingSize, fittingSize, Bitmap.Config.ARGB_8888);

        int[] pixels = new int[fittingSize * fittingSize];
        for (int r = 0; r < fittingSize; ++r) {
            for (int c = 0; c < fittingSize; ++c) {
                int pixelIdx = r * fittingSize + c;

                if (r < imageHeight && c < imageWidth)
                    pixels[pixelIdx] = origBitmap.getPixel(c, r);
                else
                    pixels[pixelIdx] = Color.rgb(0, 0, 0);
            }
        }

        scaledBitmap.setPixels(pixels, 0, fittingSize, 0, 0, fittingSize, fittingSize);
        return scaledBitmap;
    }

    public static Bitmap scaleImage_fitEnd(Bitmap origBitmap) {
        int imageWidth = origBitmap.getWidth();
        int imageHeight = origBitmap.getHeight();
        int fittingSize = imageHeight > imageWidth ? imageHeight : imageWidth;
        int blackRegionWidth = fittingSize - imageWidth;
        int blackRegionHeight = fittingSize - imageHeight;
        Bitmap scaledBitmap = Bitmap.createBitmap(fittingSize, fittingSize, Bitmap.Config.ARGB_8888);

        int[] pixels = new int[fittingSize * fittingSize];
        for (int r = 0; r < fittingSize; ++r) {
            for (int c = 0; c < fittingSize; ++c) {
                int pixelIdx = r * fittingSize + c;

                if (r < blackRegionHeight || c < blackRegionWidth)
                    pixels[pixelIdx] = Color.rgb(0, 0, 0);
                else
                    pixels[pixelIdx] = origBitmap.getPixel(c - blackRegionWidth, r - blackRegionHeight);
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

    public static void getSolidColorPixelsArray(int[] retPixels, int rowNum, int colNum, int red, int green, int blue, int alpha) {
        int color = Color.argb(alpha, red, green, blue);
        for (int r = 0; r < rowNum; ++r) {
            for (int c = 0; c < colNum; ++c) {
                int idx = r * colNum + c;
                retPixels[idx] = color;
            }
        }
    }
}
