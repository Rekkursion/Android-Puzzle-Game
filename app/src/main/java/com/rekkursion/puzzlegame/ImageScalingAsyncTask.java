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
    private TextView txtvMillisecondTimer;
    private ImageView[][] imageViews;

    private int viewHeight, viewWidth;
    private Bitmap originalBitmap;
    private int imageHeight, imageWidth;

    public void execute(ImageView[][] imageViews, ProgressBar progressBar, TextView textView, TextView txtvMillisecondTimer) {
        originalBitmap = GameManager.getInstance().originalImageBitmap;
        imageWidth = originalBitmap.getWidth();
        imageHeight = originalBitmap.getHeight();

        this.imageViews = imageViews;
        viewWidth = imageViews[0][0].getWidth();
        viewHeight = imageViews[0][0].getHeight();

        this.progressBar = progressBar;
        txtvProgressBarInformation = textView;

        this.txtvMillisecondTimer = txtvMillisecondTimer;

        super.execute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressBar.setVisibility(View.VISIBLE);
        txtvProgressBarInformation.setVisibility(View.VISIBLE);

        GameManager.getInstance().clearTagAndIdDict();
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        GameManager.getInstance().scaledImageBitmap = bitmap;
        GameManager.getInstance().shuffle();
        GameManager.getInstance().movedCount = 0;
        GameManager.getInstance().setVisibilitiesOfUIs(View.VISIBLE);
        GameManager.getInstance().gameStatus = GameManager.GameStatus.GAMING;

        progressBar.setVisibility(View.GONE);
        txtvProgressBarInformation.setVisibility(View.GONE);

        Bitmap[][] splittedBitmapsArray = ImageProcessFactory.divideImage(bitmap, GameManager.getInstance().difficulty, GameManager.getInstance().difficulty);
        for (int r = 0; r < GameManager.getInstance().difficulty; ++r) {
            for (int c = 0; c < GameManager.getInstance().difficulty; ++c) {
                int idx = GameManager.getInstance().tagNumbersMap[r][c];
                int _r = idx / GameManager.getInstance().difficulty;
                int _c = idx % GameManager.getInstance().difficulty;

                imageViews[r][c].setTag(idx);
                imageViews[r][c].setImageBitmap(splittedBitmapsArray[_r][_c]);
                if (idx == GameManager.getInstance().difficulty * GameManager.getInstance().difficulty - 1)
                    imageViews[r][c].setVisibility(View.INVISIBLE);

                int newImageViewId = View.generateViewId();
                imageViews[r][c].setId(newImageViewId);
                GameManager.getInstance().addTagAndIdEntry(idx, newImageViewId);
            }
        }

        GameManager.getInstance().initPuzzlePlayingTimerAndSetTask(txtvMillisecondTimer);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Bitmap doInBackground(Context... contexts) {
        String scaleTypeStr = GameManager.getInstance().selectedScaleTypeString;

        if (scaleTypeStr.equals("Center"))
            return ImageProcessFactory.scaleImage_fitCenter(originalBitmap);
        else if (scaleTypeStr.equals("Scaling"))
            return ImageProcessFactory.scaleImage_fitXY(originalBitmap);
        else if (scaleTypeStr.equals("Start"))
            return ImageProcessFactory.scaleImage_fitStart(originalBitmap);
        else if (scaleTypeStr.equals("End"))
            return ImageProcessFactory.scaleImage_fitEnd(originalBitmap);
        else
            return ImageProcessFactory.scaleImage_fitCenter(originalBitmap);
    }
}
