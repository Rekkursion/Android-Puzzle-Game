package com.rekkursion.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

        // get the index of new record from selected difficulty record list
        int selectedDifficulty = newRankingRecord.getGameDifficulty();

        // set up view-pager with fragments
        RankingBoardPagerAdapter adapter = new RankingBoardPagerAdapter(getSupportFragmentManager());
        // adapter.addFragment(new RankingBoardFragment(GameManager.getInstance().getRankingRecordItemListFilteredByDifficulty(3)), "3 × 3");
        for (int d = 3; d <= 7; ++d) {
            String title = String.format("%d × %d", d, d);
            List<RankingRecordItemModel> costTimeAscOrderedRecordList = sqlHelper.readData(d).stream().sorted(Comparator.comparing(RankingRecordItemModel::getCostTime)).collect(Collectors.toList());
            if (d == selectedDifficulty)
                adapter.addFragment(new RankingBoardFragment(costTimeAscOrderedRecordList, newRankingRecord), title);
            else
                adapter.addFragment(new RankingBoardFragment(costTimeAscOrderedRecordList), title);
        }
        // adapter.addFragment(new RankingBoardFragment(GameManager.getInstance().getRankingRecordItemList()), "ALL");
        vpgrDifficultiesClassification.setAdapter(adapter);

        // bind the view-pager with the tab-layout
        tblyDifficultiesClassification.setupWithViewPager(vpgrDifficultiesClassification);
        tblyDifficultiesClassification.setTabGravity(TabLayout.GRAVITY_FILL);
        tblyDifficultiesClassification.setTabMode(TabLayout.MODE_SCROLLABLE);

        // open the selected difficulty tab page
        try {
            tblyDifficultiesClassification.getTabAt(selectedDifficulty - 3).select();
        } catch (NullPointerException e) {}
    }
}
