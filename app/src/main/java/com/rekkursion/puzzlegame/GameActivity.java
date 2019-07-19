package com.rekkursion.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private ImageButton imgbtnHelpCheckOriginalScaledBitmap;
    private ImageView imgvShowOriginalScaledBitmap;
    private LinearLayout llyForShowingOriginalScaledImageAndItsUI;
    private Button btnTurnBackToGamingWhenShowingOriginalScaledBitmap;

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
        imgbtnHelpCheckOriginalScaledBitmap = findViewById(R.id.imgbtn_help_check_original_scaled_bitmap);
        imgvShowOriginalScaledBitmap = findViewById(R.id.imgv_show_original_scaled_bitmap);
        llyForShowingOriginalScaledImageAndItsUI = findViewById(R.id.lly_for_showing_original_scaled_image_and_its_ui);
        btnTurnBackToGamingWhenShowingOriginalScaledBitmap = findViewById(R.id.btn_turn_back_to_gaming_when_showing_original_scaled_bitmap);

        // initially image-view for showing original scaled image and its button are visually gone
        llyForShowingOriginalScaledImageAndItsUI.setVisibility(View.GONE);

        imgbtnHelpCheckOriginalScaledBitmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llyForShowingOriginalScaledImageAndItsUI.setVisibility(View.VISIBLE);
                glySplittedImageViewsContainer.setVisibility(View.INVISIBLE);
                imgvShowOriginalScaledBitmap.setImageBitmap(GameManager.getInstance().scaledImageBitmap);
            }
        });

        btnTurnBackToGamingWhenShowingOriginalScaledBitmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llyForShowingOriginalScaledImageAndItsUI.setVisibility(View.GONE);
                glySplittedImageViewsContainer.setVisibility(View.VISIBLE);
            }
        });
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

                final GridLayout.LayoutParams param = new GridLayout.LayoutParams();
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
                        boolean hasFinished = false;
                        if (GameManager.getInstance().isAbandonedTagNumber(upTag)) {
                            ImageView upView = findViewById(GameManager.getInstance().getImageViewIdByTag(upTag));
                            hasFinished = GameManager.getInstance().swapImageViews((ImageView) view, upView);
                        } else if (GameManager.getInstance().isAbandonedTagNumber(rightTag)) {
                            ImageView rightView = findViewById(GameManager.getInstance().getImageViewIdByTag(rightTag));
                            hasFinished = GameManager.getInstance().swapImageViews((ImageView) view, rightView);
                        } else if (GameManager.getInstance().isAbandonedTagNumber(downTag)) {
                            ImageView downView = findViewById(GameManager.getInstance().getImageViewIdByTag(downTag));
                            hasFinished = GameManager.getInstance().swapImageViews((ImageView) view, downView);
                        } else if (GameManager.getInstance().isAbandonedTagNumber(leftTag)) {
                            ImageView leftView = findViewById(GameManager.getInstance().getImageViewIdByTag(leftTag));
                            hasFinished = GameManager.getInstance().swapImageViews((ImageView) view, leftView);
                        }

                        if (hasFinished) {
                            ImageView abandonedView = findViewById(GameManager.getInstance().getImageViewIdByTag(GameManager.getInstance().getAbandonedTagNumber()));
                            abandonedView.setVisibility(View.VISIBLE);
                            abandonedView.startAnimation(AnimationUtils.loadAnimation(GameActivity.this, R.anim.fade_in));

                            // adjust the margins of all blocks without gaps (general margins)
                            int leftSideMarginWithoutGeneralMargins = (screenWidth - TOTAL_GAMING_IMAGE_VIEW_SIZE) / 2;
                            for (int r = 0; r < GameManager.getInstance().difficulty; ++r) {
                                for (int c = 0; c < GameManager.getInstance().difficulty; ++c) {
                                    ImageView imageView = findViewById(GameManager.getInstance().getImageViewIdByTag(GameManager.getInstance().tagNumbersMap[r][c]));

                                    GridLayout.LayoutParams imageViewParam = (GridLayout.LayoutParams) imageView.getLayoutParams();
                                    imageViewParam.topMargin = 0;
                                    imageViewParam.leftMargin = c == 0 ? leftSideMarginWithoutGeneralMargins : 0;
                                    imageView.setLayoutParams(imageViewParam);
                                }
                            }
                        }
                    }
                });

                glySplittedImageViewsContainer.addView(newImgView);
                imgvsSplittedBitmapsArray[r][c] = newImgView;
            }
        }
    }
}