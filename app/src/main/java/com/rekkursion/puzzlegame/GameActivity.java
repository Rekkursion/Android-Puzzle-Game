package com.rekkursion.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {
    private int TOTAL_GAMING_IMAGE_VIEW_SIZE = 1000;
    private int screenWidth, screenHeight;
    private final int marginOnBlocks = 5;
    private int marginOnLeftSideBlocks = 0;

    private ProgressBar pgbWaitForImageProcessing;
    private TextView txtvWaitForImageProcessing;
    private GridLayout glySplittedImageViewsContainer;
    private ImageView[][] imgvsSplittedBitmapsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        if (screenWidth - marginOnBlocks * 2 < TOTAL_GAMING_IMAGE_VIEW_SIZE)
            TOTAL_GAMING_IMAGE_VIEW_SIZE = screenWidth - marginOnBlocks * 2;
        int calculatedLeftSideMargin = (screenWidth - marginOnBlocks * 2 - TOTAL_GAMING_IMAGE_VIEW_SIZE) / 2;
        if (calculatedLeftSideMargin > 0)
            marginOnLeftSideBlocks = calculatedLeftSideMargin;

        initViews();
        createImageViewsForGaming();

        ImageScalingAsyncTask imageScalingAsyncTask = new ImageScalingAsyncTask();
        imageScalingAsyncTask.execute(imgvsSplittedBitmapsArray, pgbWaitForImageProcessing, txtvWaitForImageProcessing);
    }

    private void initViews() {
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
                param.topMargin = r > 0 ? marginOnBlocks : 0;
                param.leftMargin = c > 0 ? marginOnBlocks : marginOnLeftSideBlocks;
                param.rowSpec = GridLayout.spec(r);
                param.columnSpec = GridLayout.spec(c);

                newImgView.setLayoutParams(param);
                // newImgView.setTag(r * selectedDifficulty + c);

                newImgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int viewTag = (int) view.getTag();

                        if (GameManager.getInstance().isAbandonedTagNumber(viewTag))
                            return;

                        int upTag = GameManager.getInstance().getTagAroundTheCertainTag(viewTag, GameManager.Direction.UP);
                        int rightTag = GameManager.getInstance().getTagAroundTheCertainTag(viewTag, GameManager.Direction.RIGHT);
                        int downTag = GameManager.getInstance().getTagAroundTheCertainTag(viewTag, GameManager.Direction.DOWN);
                        int leftTag = GameManager.getInstance().getTagAroundTheCertainTag(viewTag, GameManager.Direction.LEFT);

                        // exchange image-views
                        if (GameManager.getInstance().isAbandonedTagNumber(upTag)) {
                            ImageView upView = findViewById(GameManager.getInstance().getImageViewIdByTag(upTag));
                            GameManager.getInstance().swapImageViews((ImageView) view, upView);
                        } else if (GameManager.getInstance().isAbandonedTagNumber(rightTag)) {
                            ImageView rightView = findViewById(GameManager.getInstance().getImageViewIdByTag(rightTag));
                            GameManager.getInstance().swapImageViews((ImageView) view, rightView);
                        } else if (GameManager.getInstance().isAbandonedTagNumber(downTag)) {
                            ImageView downView = findViewById(GameManager.getInstance().getImageViewIdByTag(downTag));
                            GameManager.getInstance().swapImageViews((ImageView) view, downView);
                        } else if (GameManager.getInstance().isAbandonedTagNumber(leftTag)) {
                            ImageView leftView = findViewById(GameManager.getInstance().getImageViewIdByTag(leftTag));
                            GameManager.getInstance().swapImageViews((ImageView) view, leftView);
                        }
                    }
                });

                glySplittedImageViewsContainer.addView(newImgView);
                imgvsSplittedBitmapsArray[r][c] = newImgView;
            }
        }
    }
}
