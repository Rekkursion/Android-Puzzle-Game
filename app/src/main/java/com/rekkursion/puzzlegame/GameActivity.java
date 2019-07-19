package com.rekkursion.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {
    public static Bitmap origImageBitmap;
    public static int selectedDifficulty;
    public static GamingModeEnum gamingMode;

    private final int TOTAL_GAMING_IMAGE_VIEW_SIZE = 1000;

    private ImageView imgvScaledImage;
    private ProgressBar pgbWaitForImageProcessing;
    private TextView txtvWaitForImageProcessing;
    private GridLayout glySplittedImageViewsContainer;
    private ImageView[][] imgvsSplittedBitmapsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initViews();
        createImageViewsForGaming();

        ImageScalingAsyncTask imageScalingAsyncTask = new ImageScalingAsyncTask();
        imageScalingAsyncTask.execute(origImageBitmap, imgvsSplittedBitmapsArray, pgbWaitForImageProcessing, txtvWaitForImageProcessing);
    }

    private void initViews() {
        imgvScaledImage = findViewById(R.id.imgv_scaled_image);
        pgbWaitForImageProcessing = findViewById(R.id.pgb_wait_for_image_processing);
        txtvWaitForImageProcessing = findViewById(R.id.txtv_wait_for_image_processing);
        glySplittedImageViewsContainer = findViewById(R.id.gly_splitted_image_views_container);
    }

    private void createImageViewsForGaming() {
        glySplittedImageViewsContainer.setRowCount(selectedDifficulty);
        glySplittedImageViewsContainer.setColumnCount(selectedDifficulty);
        imgvsSplittedBitmapsArray = new ImageView[selectedDifficulty][selectedDifficulty];

        int blockWidth = TOTAL_GAMING_IMAGE_VIEW_SIZE / selectedDifficulty;
        int blockHeight = TOTAL_GAMING_IMAGE_VIEW_SIZE / selectedDifficulty;

        for (int r = 0; r < selectedDifficulty; ++r) {
            for (int c = 0; c < selectedDifficulty; ++c) {
                ImageView newImgView = new ImageView(GameActivity.this);

                GridLayout.LayoutParams param = new GridLayout.LayoutParams();
                param.width = blockWidth;
                param.height = blockHeight;
                param.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                param.topMargin = r > 0 ? 5 : 0;
                param.leftMargin = c > 0 ? 5 : 0;
                param.rowSpec = GridLayout.spec(r);
                param.columnSpec = GridLayout.spec(c);

                newImgView.setLayoutParams(param);
                // newImgView.setImageResource(R.drawable.ic_launcher_background);

                glySplittedImageViewsContainer.addView(newImgView);
                imgvsSplittedBitmapsArray[r][c] = newImgView;
            }
        }
    }
}
