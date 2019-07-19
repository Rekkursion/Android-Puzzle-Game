package com.rekkursion.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {
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
        imageScalingAsyncTask.execute(imgvsSplittedBitmapsArray, pgbWaitForImageProcessing, txtvWaitForImageProcessing);
    }

    private void initViews() {
        imgvScaledImage = findViewById(R.id.imgv_scaled_image);
        pgbWaitForImageProcessing = findViewById(R.id.pgb_wait_for_image_processing);
        txtvWaitForImageProcessing = findViewById(R.id.txtv_wait_for_image_processing);
        glySplittedImageViewsContainer = findViewById(R.id.gly_splitted_image_views_container);
    }

    private void createImageViewsForGaming() {
        int selectedDifficulty = GameManager.getInstance().difficulty;

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
                newImgView.setTag(r * selectedDifficulty + c);
                // newImgView.setImageResource(R.drawable.ic_launcher_background);

                newImgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int viewIdx = (int) view.getTag();

                        if (viewIdx == GameManager.getInstance().getIdxOfAbandonedBitmap())
                            return;

                        int upIdx = viewIdx - GameManager.getInstance().difficulty;
                        int rightIdx = viewIdx + 1;
                        int downIdx = viewIdx + GameManager.getInstance().difficulty;
                        int leftIdx = viewIdx - 1;

                        if (upIdx == GameManager.getInstance().getIdxOfAbandonedBitmap()) {
                            GameManager.getInstance().exchangeBlocks(viewIdx, upIdx);

                            int rowIdx_up = upIdx / GameManager.getInstance().difficulty;
                            int colIdx_up = upIdx % GameManager.getInstance().difficulty;
                            int rowIdx_view = viewIdx / GameManager.getInstance().difficulty;
                            int colIdx_view = viewIdx % GameManager.getInstance().difficulty;

                            Bitmap tmp = ((BitmapDrawable) imgvsSplittedBitmapsArray[rowIdx_up][colIdx_up].getDrawable()).getBitmap();
                            imgvsSplittedBitmapsArray[rowIdx_up][colIdx_up].setImageBitmap(((BitmapDrawable) imgvsSplittedBitmapsArray[rowIdx_view][colIdx_view].getDrawable()).getBitmap());
                            imgvsSplittedBitmapsArray[rowIdx_view][colIdx_view].setImageBitmap(tmp);
                        }
                    }
                });

                glySplittedImageViewsContainer.addView(newImgView);
                imgvsSplittedBitmapsArray[r][c] = newImgView;
            }
        }
    }
}
