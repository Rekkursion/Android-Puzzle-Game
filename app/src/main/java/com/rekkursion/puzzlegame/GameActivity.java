package com.rekkursion.puzzlegame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.Game;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class GameActivity extends AppCompatActivity {
    private final int REQ_CODE_TO_RANKING_ACTIVITY = 13;

    private int TOTAL_GAMING_IMAGE_VIEW_SIZE = 1000;
    private int screenWidth, screenHeight;
    private final int marginOnBlocks = 5;
    private int marginOnLeftSideBlocks = 0;

    private ProgressBar pgbWaitForImageProcessing;
    private TextView txtvWaitForImageProcessing;
    private GridLayout glySplittedImageViewsContainer;
    private ImageButton imgbtnHelpCheckOriginalScaledBitmap;
    private ImageView imgvShowOriginalScaledBitmap;
    private ImageView imgvShowLinesOfOriginalScaledBitmap;
    private LinearLayout llyForShowingOriginalScaledImageAndItsUI;
    private Button btnTurnBackToGamingWhenShowingOriginalScaledBitmap;
    private TextView btnGiveUpWhenShowingOriginalScaledBitmap;
    private TextView btnBackToMenuWhenShowingOriginalScaledBitmap;
    private TextView txtvTapCounter;
    private TextView txtvMillisecondTimer;

    private ImageView[][] imgvsSplittedBitmapsArray;

    // on-click-listener for giving up or backing to menu
    private View.OnClickListener giveUpOrBackToMenuButtonOnClickListener = view -> {
        SoundPoolManager.getInstance().play("se_maoudamashii_click_leaving.mp3");

        new AlertDialog.Builder(GameActivity.this)
                .setIcon(R.drawable.ic_warning_orange_24dp)
                .setMessage(R.string.str_user_check_before_give_up_the_game)
                .setPositiveButton(R.string.str_user_check_yes, (dialogInterface, i) -> {
                    BackgroundMusicManager.shouldStopPlayingWhenLeaving = false;
                    BackgroundMusicManager.getInstance(GameActivity.this).stop();
                    BackgroundMusicManager.getInstance(GameActivity.this).play("musics" + File.separator + "game_maoudamashii_main_theme.mp3", true);

                    BackToWhere backToWhere = view.getId() == R.id.txtv_give_up_button_when_showing_original_scaled_bitmap ? BackToWhere.BACK_TO_LEVEL_SELECT : BackToWhere.BACK_TO_MENU;
                    setResult(backToWhere.ordinal());
                    finish();
                })
                .setNegativeButton(R.string.str_user_check_no, null)
                .show();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set transition animations (entering game)
        getWindow().setEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.slide_end));
        getWindow().setReturnTransition(TransitionInflater.from(this).inflateTransition(R.transition.slide_end));

        // set transition animations (returning back)
//        getWindow().setExitTransition(new Slide(Gravity.START).setDuration(MainActivity.TRANS_ANIM_DURA));
//        getWindow().setReenterTransition(new Slide(Gravity.START).setDuration(MainActivity.TRANS_ANIM_DURA));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // readjust the size and the margins of blocks
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        int numOfGap = GameManager.getInstance().difficulty - 1;
        if (screenWidth - marginOnBlocks * numOfGap < TOTAL_GAMING_IMAGE_VIEW_SIZE)
            TOTAL_GAMING_IMAGE_VIEW_SIZE = screenWidth - marginOnBlocks * numOfGap;
        int calculatedLeftSideMargin = (screenWidth - marginOnBlocks * numOfGap - TOTAL_GAMING_IMAGE_VIEW_SIZE) / 2;
        if (calculatedLeftSideMargin > 0)
            marginOnLeftSideBlocks = calculatedLeftSideMargin;

        // init xml views and create image-views for the puzzle
        initViews();
        createImageViewsForGaming();

        // init game status
        GameManager.getInstance().gameStatus = GameManager.GameStatus.PRE;

        // start async task for image scaling
        ImageScalingAsyncTask imageScalingAsyncTask = new ImageScalingAsyncTask();
        imageScalingAsyncTask.execute(imgvsSplittedBitmapsArray, pgbWaitForImageProcessing, txtvWaitForImageProcessing, txtvMillisecondTimer);
    }

    @Override
    protected void onPause() {
        if (BackgroundMusicManager.shouldStopPlayingWhenLeaving)
            BackgroundMusicManager.getInstance(this).pause();

        super.onPause();
    }

    @Override
    protected void onResume() {
        if (BackgroundMusicManager.shouldStopPlayingWhenLeaving)
            BackgroundMusicManager.getInstance(this).resume();
        BackgroundMusicManager.shouldStopPlayingWhenLeaving = true;

        super.onResume();
    }

    @Override
    protected void onStop() {
        GameManager.getInstance().puzzerPlayingTimerStatus = GameManager.TimerStatus.PAUSED;
        super.onStop();
    }

    @Override
    protected void onRestart() {
        imgbtnHelpCheckOriginalScaledBitmap.callOnClick();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GameManager.getInstance().puzzerPlayingTimerStatus = GameManager.TimerStatus.STOPPED;
    }

    @Override
    public void onBackPressed() {
        SoundPoolManager.getInstance().play("se_maoudamashii_click_leaving.mp3");

        if (GameManager.getInstance().puzzerPlayingTimerStatus == GameManager.TimerStatus.PAUSED)
            btnTurnBackToGamingWhenShowingOriginalScaledBitmap.callOnClick();
        else if (GameManager.getInstance().puzzerPlayingTimerStatus == GameManager.TimerStatus.RUNNING)
            imgbtnHelpCheckOriginalScaledBitmap.callOnClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_TO_RANKING_ACTIVITY:
                // directly finish whatever back-to-menu or back-to-level-select
                setResult(resultCode);
                finish();
                break;
        }
    }

    private void initViews() {
        pgbWaitForImageProcessing = findViewById(R.id.pgb_wait_for_image_processing);
        txtvWaitForImageProcessing = findViewById(R.id.txtv_wait_for_image_processing);
        glySplittedImageViewsContainer = findViewById(R.id.gly_splitted_image_views_container);
        imgbtnHelpCheckOriginalScaledBitmap = findViewById(R.id.imgbtn_help_check_original_scaled_bitmap);
        imgvShowOriginalScaledBitmap = findViewById(R.id.imgv_show_original_scaled_bitmap);
        imgvShowLinesOfOriginalScaledBitmap = findViewById(R.id.imgv_show_lines_of_original_scaled_bitmap);
        llyForShowingOriginalScaledImageAndItsUI = findViewById(R.id.lly_for_showing_original_scaled_image_and_its_ui);
        btnTurnBackToGamingWhenShowingOriginalScaledBitmap = findViewById(R.id.btn_turn_back_to_gaming_when_showing_original_scaled_bitmap);
        btnGiveUpWhenShowingOriginalScaledBitmap = findViewById(R.id.txtv_give_up_button_when_showing_original_scaled_bitmap);
        btnBackToMenuWhenShowingOriginalScaledBitmap = findViewById(R.id.txtv_back_to_menu_button_when_showing_original_scaled_bitmap);
        txtvTapCounter = findViewById(R.id.txtv_tap_counter);
        txtvMillisecondTimer = findViewById(R.id.txtv_millisecond_timer);

        // adjust size of image-view which is used to show the original scaled bitmap
        imgvShowOriginalScaledBitmap.getLayoutParams().width = TOTAL_GAMING_IMAGE_VIEW_SIZE;
        imgvShowOriginalScaledBitmap.getLayoutParams().height = TOTAL_GAMING_IMAGE_VIEW_SIZE;
        imgvShowLinesOfOriginalScaledBitmap.getLayoutParams().width = TOTAL_GAMING_IMAGE_VIEW_SIZE;
        imgvShowLinesOfOriginalScaledBitmap.getLayoutParams().height = TOTAL_GAMING_IMAGE_VIEW_SIZE;

        // discover UIs when the image is processing
        discoverUIsWhenProcessingImage();

        // draw lines of original scaled image
        Bitmap hintLinesBitmap = ImageProcessFactory.getHintLinesBitmap(imgvShowOriginalScaledBitmap.getLayoutParams().width, imgvShowOriginalScaledBitmap.getLayoutParams().height, GameManager.getInstance().difficulty);
        imgvShowLinesOfOriginalScaledBitmap.setImageBitmap(hintLinesBitmap);

        // initially image-view for showing original scaled image and its button are visually gone
        llyForShowingOriginalScaledImageAndItsUI.setVisibility(View.GONE);

        imgbtnHelpCheckOriginalScaledBitmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundPoolManager.getInstance().play("se_maoudamashii_click_entering.mp3");

                llyForShowingOriginalScaledImageAndItsUI.setVisibility(View.VISIBLE);
                glySplittedImageViewsContainer.setVisibility(View.INVISIBLE);
                imgvShowOriginalScaledBitmap.setImageBitmap(GameManager.getInstance().scaledImageBitmap);

                GameManager.getInstance().setVisibilitiesOfUIs(View.GONE);

                if (GameManager.getInstance().puzzerPlayingTimerStatus == GameManager.TimerStatus.RUNNING)
                    GameManager.getInstance().puzzerPlayingTimerStatus = GameManager.TimerStatus.PAUSED;
            }
        });

        // show/hide hint lines on the original scaled bitmap
        imgvShowLinesOfOriginalScaledBitmap.setOnClickListener(view -> imgvShowLinesOfOriginalScaledBitmap.setVisibility(View.GONE));
        imgvShowOriginalScaledBitmap.setOnClickListener(view -> imgvShowLinesOfOriginalScaledBitmap.setVisibility(View.VISIBLE));

        btnTurnBackToGamingWhenShowingOriginalScaledBitmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoundPoolManager.getInstance().play("se_maoudamashii_click_leaving.mp3");

                llyForShowingOriginalScaledImageAndItsUI.setVisibility(View.GONE);
                glySplittedImageViewsContainer.setVisibility(View.VISIBLE);

                GameManager.getInstance().setVisibilitiesOfUIs(View.VISIBLE);

                if (GameManager.getInstance().puzzerPlayingTimerStatus == GameManager.TimerStatus.PAUSED)
                    GameManager.getInstance().puzzerPlayingTimerStatus = GameManager.TimerStatus.RUNNING;
            }
        });

        btnGiveUpWhenShowingOriginalScaledBitmap.setOnClickListener(giveUpOrBackToMenuButtonOnClickListener);
        btnBackToMenuWhenShowingOriginalScaledBitmap.setOnClickListener(giveUpOrBackToMenuButtonOnClickListener);
    }

    private void discoverUIsWhenProcessingImage() {
        GameManager.getInstance().clearUIList();

        imgbtnHelpCheckOriginalScaledBitmap.setVisibility(View.GONE);
        GameManager.getInstance().addUIWhichShouldBeDiscoveredWhenProcessingImage(imgbtnHelpCheckOriginalScaledBitmap);

        txtvTapCounter.setVisibility(View.GONE);
        GameManager.getInstance().addUIWhichShouldBeDiscoveredWhenProcessingImage(txtvTapCounter);

        txtvMillisecondTimer.setVisibility(View.GONE);
        GameManager.getInstance().addUIWhichShouldBeDiscoveredWhenProcessingImage(txtvMillisecondTimer);
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
                        if (GameManager.getInstance().gameStatus != GameManager.GameStatus.GAMING)
                            return;

                        int viewTag = (int) view.getTag();

                        if (GameManager.getInstance().isAbandonedTagNumber(viewTag))
                            return;

                        int upTag = GameManager.getInstance().getTagAroundTheCertainTag(viewTag, GameManager.Direction.UP);
                        int rightTag = GameManager.getInstance().getTagAroundTheCertainTag(viewTag, GameManager.Direction.RIGHT);
                        int downTag = GameManager.getInstance().getTagAroundTheCertainTag(viewTag, GameManager.Direction.DOWN);
                        int leftTag = GameManager.getInstance().getTagAroundTheCertainTag(viewTag, GameManager.Direction.LEFT);

                        // exchange image-views
                        boolean hasFinished = false;
                        boolean moved = false;
                        if (GameManager.getInstance().isAbandonedTagNumber(upTag)) {
                            ImageView upView = findViewById(GameManager.getInstance().getImageViewIdByTag(upTag));
                            hasFinished = GameManager.getInstance().swapImageViews((ImageView) view, upView);
                            moved = true;
                        } else if (GameManager.getInstance().isAbandonedTagNumber(rightTag)) {
                            ImageView rightView = findViewById(GameManager.getInstance().getImageViewIdByTag(rightTag));
                            hasFinished = GameManager.getInstance().swapImageViews((ImageView) view, rightView);
                            moved = true;
                        } else if (GameManager.getInstance().isAbandonedTagNumber(downTag)) {
                            ImageView downView = findViewById(GameManager.getInstance().getImageViewIdByTag(downTag));
                            hasFinished = GameManager.getInstance().swapImageViews((ImageView) view, downView);
                            moved = true;
                        } else if (GameManager.getInstance().isAbandonedTagNumber(leftTag)) {
                            ImageView leftView = findViewById(GameManager.getInstance().getImageViewIdByTag(leftTag));
                            hasFinished = GameManager.getInstance().swapImageViews((ImageView) view, leftView);
                            moved = true;
                        }

                        // if moved successfully, plus one into the counter
                        if (moved) {
                            int newTappedCount = ++GameManager.getInstance().movedCount;
                            String tapped_0 = getString(R.string.str_tapped_0);
                            String tapped = tapped_0.substring(0, tapped_0.indexOf(" "));
                            txtvTapCounter.setText(tapped + " " + String.valueOf(newTappedCount));

                            SoundPoolManager.getInstance().play("se_maoudamashii_move_piece_successfully.mp3");
                        }
                        else
                            SoundPoolManager.getInstance().play("se_maoudamashii_move_piece_failed.mp3");

                        if (hasFinished) {
                            // switch bgm
                            BackgroundMusicManager.shouldStopPlayingWhenLeaving = false;
                            BackgroundMusicManager.getInstance(GameActivity.this).stop();
                            BackgroundMusicManager.getInstance(GameActivity.this).play("musics" + File.separator + "game_maoudamashii_ranking_theme.mp3", true);

                            // find abandoned view and visualized it
                            ImageView abandonedView = findViewById(GameManager.getInstance().getImageViewIdByTag(GameManager.getInstance().getAbandonedTagNumber()));
                            abandonedView.setVisibility(View.VISIBLE);

                            // set animation
                            Animation finishAnim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.fade_in);
                            finishAnim.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    GameManager.getInstance().gameStatus = GameManager.GameStatus.POST;
                                    GameManager.getInstance().puzzerPlayingTimerStatus = GameManager.TimerStatus.STOPPED;

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

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    goToRankingActivity();
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {}
                            });
                            abandonedView.startAnimation(finishAnim);
                        }
                    }
                });

                glySplittedImageViewsContainer.addView(newImgView);
                imgvsSplittedBitmapsArray[r][c] = newImgView;
            }
        }
    }

    private void goToRankingActivity() {
        // get cost time and current date
        double costTime_double = Double.valueOf(txtvMillisecondTimer.getText().toString());
        int costTime_int = (int) (costTime_double * 100.0);
        Date now = Calendar.getInstance().getTime();

        // set new ranking record at ranking-activity
        if (RankingActivity.newRankingRecord != null)
            RankingActivity.newRankingRecord = null;
        RankingActivity.newRankingRecord = new RankingRecordItemModel(GameManager.getInstance().difficulty, GameManager.getInstance().movedCount, costTime_int, now);

        // create intent and go to ranking-activity
        Intent intentToRankingActivity = new Intent(GameActivity.this, RankingActivity.class);
        startActivityForResult(intentToRankingActivity, REQ_CODE_TO_RANKING_ACTIVITY, ActivityOptions.makeSceneTransitionAnimation(GameActivity.this).toBundle());
    }
}
