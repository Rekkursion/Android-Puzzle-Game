package com.rekkursion.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

public class RankingActivity extends AppCompatActivity {
    private TabLayout tblyDifficultiesClassification;
    private ViewPager vpgrDifficultiesClassification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        initViews();
    }

    private void initViews() {
        tblyDifficultiesClassification = findViewById(R.id.tbly_difficulties_classification);
        vpgrDifficultiesClassification = findViewById(R.id.vpgr_difficulties_classification);

        // bind the view-pager with the tab-layout
        tblyDifficultiesClassification.setupWithViewPager(vpgrDifficultiesClassification);

        // set up view-pager with fragments
        RankingBoardPagerAdapter adapter = new RankingBoardPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RankingBoardFragment(GameManager.getInstance().getRankingRecordItemListFilteredByDifficulty(3)), "3 x 3");
        adapter.addFragment(new RankingBoardFragment(GameManager.getInstance().getRankingRecordItemListFilteredByDifficulty(4)), "4 x 4");
        adapter.addFragment(new RankingBoardFragment(GameManager.getInstance().getRankingRecordItemListFilteredByDifficulty(5)), "5 x 5");
        vpgrDifficultiesClassification.setAdapter(adapter);
    }
}
