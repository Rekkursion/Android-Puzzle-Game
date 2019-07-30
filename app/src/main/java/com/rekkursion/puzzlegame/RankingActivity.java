package com.rekkursion.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RankingActivity extends AppCompatActivity {
    private TabLayout tblyDifficultiesClassification;
    private ViewPager vpgrDifficultiesClassification;

    private TextView txtvTryAgain;
    private TextView txtvBackToMenu;

    private SQLiteDatabaseHelper sqlHelper;
    public static RankingRecordItemModel newRankingRecord;

    private View.OnTouchListener textViewsAsButtonsOnTouchListener = (view, motionEvent) -> {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Animation animOptionPressed = AnimationUtils.loadAnimation(RankingActivity.this, R.anim.scale_animation_view_pressed);
                animOptionPressed.setFillAfter(true);
                view.startAnimation(animOptionPressed);
                return true;

            case MotionEvent.ACTION_UP:
                txtvTryAgain.setEnabled(false);
                txtvBackToMenu.setEnabled(false);

                Animation animOptionUnpressed = AnimationUtils.loadAnimation(RankingActivity.this, R.anim.scale_animation_view_unpressed);
                animOptionUnpressed.setFillAfter(true);
                animOptionUnpressed.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view.performClick();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                view.startAnimation(animOptionUnpressed);
                return true;

            default: return true;
        }
    };

    private View.OnClickListener menuOptionsOnClickListener = view -> {
        switch (view.getId()) {
            // try again (back to level-select)
            case R.id.txtv_try_again_at_ranking_activity:
                setResult(BackToWhere.BACK_TO_LEVEL_SELECT.ordinal());
                finish();
                break;

            // back to menu
            case R.id.txtv_back_to_menu_button_at_ranking_activity:
                setResult(BackToWhere.BACK_TO_MENU.ordinal());
                finish();
                break;
        }

        BackgroundMusicManager.shouldStopPlayingWhenLeaving = false;
        BackgroundMusicManager.getInstance(this).stop();
        BackgroundMusicManager.getInstance(this).play("musics" + File.separator + "game_maoudamashii_main_theme.mp3", true);

        SoundPoolManager.getInstance().play("se_maoudamashii_click_leaving.mp3");
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        sqlHelper = new SQLiteDatabaseHelper(this);
        if (newRankingRecord != null)
            sqlHelper.insertData(newRankingRecord);
        initViews();
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

        txtvTryAgain.setEnabled(true);
        txtvBackToMenu.setEnabled(true);

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        BackgroundMusicManager.shouldStopPlayingWhenLeaving = false;
        BackgroundMusicManager.getInstance(this).stop();
        BackgroundMusicManager.getInstance(this).play("musics" + File.separator + "game_maoudamashii_main_theme.mp3", true);

        setResult(BackToWhere.BACK_TO_LEVEL_SELECT.ordinal());
        finish();
    }

    private void initViews() {
        tblyDifficultiesClassification = findViewById(R.id.tbly_difficulties_classification);
        vpgrDifficultiesClassification = findViewById(R.id.vpgr_difficulties_classification);

        txtvTryAgain = findViewById(R.id.txtv_try_again_at_ranking_activity);
        txtvBackToMenu = findViewById(R.id.txtv_back_to_menu_button_at_ranking_activity);

        // get the index of new record from selected difficulty record list
        int selectedDifficulty = newRankingRecord == null ? -1 : newRankingRecord.getGameDifficulty();

        // set up view-pager with fragments
        RankingBoardPagerAdapter adapter = new RankingBoardPagerAdapter(getSupportFragmentManager());
        // adapter.addFragment(new RankingBoardFragment(GameManager.getInstance().getRankingRecordItemListFilteredByDifficulty(3)), "3 × 3");
        for (int d = 3; d <= 7; ++d) {
            String title = String.format("%d × %d", d, d);
            List<RankingRecordItemModel> costTimeAscOrderedRecordList = sqlHelper.readData(d).stream().sorted(Comparator.comparing(RankingRecordItemModel::getCostTime)).collect(Collectors.toList());
            if (d == selectedDifficulty)
                adapter.addFragment(new RankingBoardFragment(costTimeAscOrderedRecordList, sqlHelper, newRankingRecord), title);
            else
                adapter.addFragment(new RankingBoardFragment(costTimeAscOrderedRecordList, sqlHelper), title);
        }
        // adapter.addFragment(new RankingBoardFragment(GameManager.getInstance().getRankingRecordItemList()), "ALL");
        vpgrDifficultiesClassification.setAdapter(adapter);

        // bind the view-pager with the tab-layout
        tblyDifficultiesClassification.setupWithViewPager(vpgrDifficultiesClassification);
        tblyDifficultiesClassification.setTabGravity(TabLayout.GRAVITY_FILL);
        tblyDifficultiesClassification.setTabMode(TabLayout.MODE_SCROLLABLE);

        // add tab-selected-listener on the tab-layout for sounding
        tblyDifficultiesClassification.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                SoundPoolManager.getInstance().play("se_maoudamashii_ranking_tab_click.mp3");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // open the selected difficulty tab page
        try {
            tblyDifficultiesClassification.getTabAt(selectedDifficulty - 3).select();
        } catch (NullPointerException e) {}

        // set on-touch-listener for text-views as buttons
        txtvTryAgain.setOnTouchListener(textViewsAsButtonsOnTouchListener);
        txtvBackToMenu.setOnTouchListener(textViewsAsButtonsOnTouchListener);

        // set on-click-listener for text-views as buttons
        txtvTryAgain.setOnClickListener(menuOptionsOnClickListener);
        txtvBackToMenu.setOnClickListener(menuOptionsOnClickListener);

        // if comes from menu-activity, don't show try-again button
        if (newRankingRecord == null)
            txtvTryAgain.setVisibility(View.GONE);
    }
}
