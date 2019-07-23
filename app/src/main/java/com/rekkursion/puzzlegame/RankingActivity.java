package com.rekkursion.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

public class RankingActivity extends AppCompatActivity {
    private TabLayout tblyDifficultiesClassification;
    private ViewPager vpgrDifficultiesClassification;

    private SQLiteDatabaseHelper sqlHelper;
    public static RankingRecordItemModel newRankingRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        sqlHelper = new SQLiteDatabaseHelper(this);
        sqlHelper.insertData(newRankingRecord);
        initViews();
    }

    private void initViews() {
        tblyDifficultiesClassification = findViewById(R.id.tbly_difficulties_classification);
        vpgrDifficultiesClassification = findViewById(R.id.vpgr_difficulties_classification);

        // set up view-pager with fragments
        RankingBoardPagerAdapter adapter = new RankingBoardPagerAdapter(getSupportFragmentManager());
        // adapter.addFragment(new RankingBoardFragment(GameManager.getInstance().getRankingRecordItemListFilteredByDifficulty(3)), "3 × 3");
        adapter.addFragment(new RankingBoardFragment(sqlHelper.readData(3)), "3 × 3");
        adapter.addFragment(new RankingBoardFragment(sqlHelper.readData(4)), "4 × 4");
        adapter.addFragment(new RankingBoardFragment(sqlHelper.readData(5)), "5 × 5");
        adapter.addFragment(new RankingBoardFragment(sqlHelper.readData(6)), "6 × 6");
        adapter.addFragment(new RankingBoardFragment(sqlHelper.readData(7)), "7 × 7");
        // adapter.addFragment(new RankingBoardFragment(GameManager.getInstance().getRankingRecordItemList()), "ALL");
        vpgrDifficultiesClassification.setAdapter(adapter);

        // bind the view-pager with the tab-layout
        tblyDifficultiesClassification.setupWithViewPager(vpgrDifficultiesClassification);
        tblyDifficultiesClassification.setTabGravity(TabLayout.GRAVITY_FILL);
        tblyDifficultiesClassification.setTabMode(TabLayout.MODE_SCROLLABLE);
    }
}
