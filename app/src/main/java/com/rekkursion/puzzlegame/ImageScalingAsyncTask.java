package com.rekkursion.puzzlegame;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ImageScalingAsyncTask extends AsyncTask<Context, Integer, Bitmap> {
    private ProgressBar progressBar;
    private TextView txtvProgressBarInformation;
    private ImageView[][] imageViews;

    private int viewHeight, viewWidth;
    private Bitmap originalBitmap;
    private int imageHeight, imageWidth;

    public void execute(ImageView[][] imageViews, ProgressBar progressBar, TextView textView) {
        originalBitmap = GameManager.getInstance().originalImageBitmap;
        imageWidth = originalBitmap.getWidth();
        imageHeight = originalBitmap.getHeight();

        this.imageViews = imageViews;
        viewWidth = imageViews[0][0].getWidth();
        viewHeight = imageViews[0][0].getHeight();

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

        GameManager.getInstance().scaledImageBitmap = bitmap;
        GameManager.getInstance().splittedBitmapsArray = ImageProcessFactory.divideImage(bitmap, GameManager.getInstance().difficulty, GameManager.getInstance().difficulty);
        GameManager.getInstance().lastSplittedBitmap =GameManager.getInstance().splittedBitmapsArray[GameManager.getInstance().difficulty - 1][GameManager.getInstance().difficulty - 1];

        Bitmap[][] shuffled = GameManager.getInstance().abandonTheLastBitmapAndShuffle();

        for (int r = 0; r < GameManager.getInstance().splittedBitmapsArray.length; ++r)
            for (int c = 0; c < GameManager.getInstance().splittedBitmapsArray[r].length; ++c)
                imageViews[r][c].setImageBitmap(shuffled[r][c]);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Bitmap doInBackground(Context... contexts) {
        int biggerSize = imageHeight > imageWidth ? imageHeight : imageWidth;
        return ImageProcessFactory.scaleImage_fitCenter(originalBitmap, biggerSize);
    }
}
