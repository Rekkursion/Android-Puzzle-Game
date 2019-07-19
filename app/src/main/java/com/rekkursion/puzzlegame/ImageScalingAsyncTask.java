package com.rekkursion.puzzlegame;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ImageScalingAsyncTask extends AsyncTask<Context, Integer, Bitmap> {
    private ProgressBar progressBar;
    private TextView txtvProgressBarInformation;
    private ImageView imageView;

    private int viewHeight, viewWidth;
    private Bitmap orginalBitmap;
    private int imageHeight, imageWidth;

    public void execute(Bitmap imageBitmap, ImageView imageView, ProgressBar progressBar, TextView textView) {
        orginalBitmap = imageBitmap;
        imageWidth = imageBitmap.getWidth();
        imageHeight = imageBitmap.getHeight();

        this.imageView = imageView;
        viewWidth = imageView.getWidth();
        viewHeight = imageView.getHeight();

        this.progressBar = progressBar;
        txtvProgressBarInformation = textView;

        super.execute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
        txtvProgressBarInformation.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        progressBar.setVisibility(View.GONE);
        txtvProgressBarInformation.setVisibility(View.GONE);

        imageView.setImageBitmap(bitmap);
        Log.e("tag", "async task end");
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Bitmap doInBackground(Context... contexts) {
        int biggerSize = imageHeight > imageWidth ? imageHeight : imageWidth;
        Bitmap scaledBitmap = Bitmap.createBitmap(biggerSize, biggerSize, Bitmap.Config.ARGB_8888);

        // step 1: fill in with black pixels
        int[] pixels = new int[biggerSize * biggerSize];
        for (int r = 0; r < biggerSize; ++r) {
            for (int c = 0; c < biggerSize; ++c) {
                int idx = r * biggerSize + c;
                pixels[idx] = 0xff000000;
            }
        }

        // step 2: scale the original bitmap (current method is fit-center)
        int startRowIdx = biggerSize == imageHeight ? 0 : (biggerSize - imageHeight) / 2;
        int startColIdx = biggerSize == imageWidth ? 0 : (biggerSize - imageWidth) / 2;
        for (int r = startRowIdx; r < startRowIdx + imageHeight; ++r) {
            for (int c = startColIdx; c < startColIdx + imageWidth; ++c) {
                int idx = r * biggerSize + c;
                pixels[idx] = orginalBitmap.getPixel(c - startColIdx, r - startRowIdx);
            }
        }

        scaledBitmap.setPixels(pixels, 0, biggerSize, 0, 0, biggerSize, biggerSize);
        return scaledBitmap;
    }
}
