package com.rekkursion.puzzlegame;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


public class RankingBoardFragment extends Fragment {
    private enum RankingBoardOrderingStatus {
        PLACE_ASC, PLACE_DESC,
        COST_TIME_ASC, COST_TIME_DESC,
        MOVED_COUNT_ASC, MOVED_COUNT_DESC,
        DATE_ASC, DATE_DESC
    }

    private RecyclerView recvRankingBoard;
    private List<RankingRecordItemModel> rankingRecordItemList;

    private TextView txtvRankingBoardTitlePlace;
    private TextView txtvRankingBoardTitleCostTime;
    private TextView txtvRankingBoardTitleMovedCount;
    private TextView txtvRankingBoardTitleDate;

    private RankingBoardOrderingStatus orderStatus;

    public RankingBoardFragment(List<RankingRecordItemModel> list) {
        super();
        this.rankingRecordItemList = list;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ranking_board, container, false);

        recvRankingBoard = root.findViewById(R.id.recv_ranking_board);

        txtvRankingBoardTitlePlace = root.findViewById(R.id.txtv_ranking_board_title_place);
        txtvRankingBoardTitleCostTime = root.findViewById(R.id.txtv_ranking_board_title_cost_time);
        txtvRankingBoardTitleMovedCount = root.findViewById(R.id.txtv_ranking_board_title_moved_count);
        txtvRankingBoardTitleDate = root.findViewById(R.id.txtv_ranking_board_title_record_done_date);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // build layout-manager for recycler-view
        LinearLayoutManager llyManager = new LinearLayoutManager(RankingBoardFragment.this.getContext());
        llyManager.setOrientation(RecyclerView.VERTICAL);

        // set layout-manager
        recvRankingBoard.setLayoutManager(llyManager);

        // set adapter for recycler-view
        RankingRecordItemAdapter rrItemAdapter = new RankingRecordItemAdapter(this.rankingRecordItemList);
        recvRankingBoard.setAdapter(rrItemAdapter);
        rrItemAdapter.notifyDataSetChanged();

        // set default status of ranking board ordering
        orderStatus = RankingBoardOrderingStatus.PLACE_ASC;

        // set on-click-listener for titles of recycler-view
        txtvRankingBoardTitlePlace.setOnClickListener(rankingBoardTitlesOnClickListener);
        txtvRankingBoardTitleCostTime.setOnClickListener(rankingBoardTitlesOnClickListener);
        txtvRankingBoardTitleMovedCount.setOnClickListener(rankingBoardTitlesOnClickListener);
        txtvRankingBoardTitleDate.setOnClickListener(rankingBoardTitlesOnClickListener);

        // not showing txtv-ranking-board-title-place since i have no idea how to deal with ranking places
        txtvRankingBoardTitlePlace.setVisibility(View.GONE);
        ViewGroup.LayoutParams param = txtvRankingBoardTitleCostTime.getLayoutParams();

    }

    // ranking board titles click events: ordering features
    private View.OnClickListener rankingBoardTitlesOnClickListener = view -> {
        RankingBoardOrderingStatus previousStatus = orderStatus;

        switch (view.getId()) {
            case R.id.txtv_ranking_board_title_place:
                if (orderStatus == RankingBoardOrderingStatus.PLACE_ASC) {
                    this.rankingRecordItemList = this.rankingRecordItemList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                    orderStatus = RankingBoardOrderingStatus.PLACE_DESC;
                } else {
                    this.rankingRecordItemList = this.rankingRecordItemList.stream().sorted().collect(Collectors.toList());
                    orderStatus = RankingBoardOrderingStatus.PLACE_ASC;
                }
                break;

            case R.id.txtv_ranking_board_title_cost_time:
                if (orderStatus == RankingBoardOrderingStatus.COST_TIME_ASC) {
                    this.rankingRecordItemList = this.rankingRecordItemList.stream().sorted(Comparator.comparing(RankingRecordItemModel::getCostTime).reversed()).collect(Collectors.toList());
                    orderStatus = RankingBoardOrderingStatus.COST_TIME_DESC;
                } else {
                    this.rankingRecordItemList = this.rankingRecordItemList.stream().sorted(Comparator.comparing(RankingRecordItemModel::getCostTime)).collect(Collectors.toList());
                    orderStatus = RankingBoardOrderingStatus.COST_TIME_ASC;
                }
                break;

            case R.id.txtv_ranking_board_title_moved_count:
                if (orderStatus == RankingBoardOrderingStatus.MOVED_COUNT_ASC) {
                    this.rankingRecordItemList = this.rankingRecordItemList.stream().sorted(Comparator.comparing(RankingRecordItemModel::getMovedCount).reversed()).collect(Collectors.toList());
                    orderStatus = RankingBoardOrderingStatus.MOVED_COUNT_DESC;
                } else {
                    this.rankingRecordItemList = this.rankingRecordItemList.stream().sorted(Comparator.comparing(RankingRecordItemModel::getMovedCount)).collect(Collectors.toList());
                    orderStatus = RankingBoardOrderingStatus.MOVED_COUNT_ASC;
                }
                break;

            case R.id.txtv_ranking_board_title_record_done_date:
                if (orderStatus == RankingBoardOrderingStatus.DATE_ASC) {
                    this.rankingRecordItemList = this.rankingRecordItemList
                            .stream()
                            .sorted((lhs, rhs) -> {
                                Date ld = lhs.getRecordDate();
                                Date rd = rhs.getRecordDate();
                                return ld.before(rd) ? 1 : -1;
                            })
                            .collect(Collectors.toList());
                    orderStatus = RankingBoardOrderingStatus.DATE_DESC;
                } else {
                    this.rankingRecordItemList = this.rankingRecordItemList
                            .stream()
                            .sorted((lhs, rhs) -> {
                                Date ld = lhs.getRecordDate();
                                Date rd = rhs.getRecordDate();
                                Log.e("ld", ld.toString());
                                Log.e("rd", rd.toString());
                                return ld.before(rd) ? -1 : 1;
                            })
                            .collect(Collectors.toList());
                    orderStatus = RankingBoardOrderingStatus.DATE_ASC;
                }
                break;
        }

        // reset item-adapter for recycler-view
        RankingRecordItemAdapter rrItemAdapter = new RankingRecordItemAdapter(this.rankingRecordItemList);
        // recvRankingBoard.swapAdapter(rrItemAdapter, true);
        recvRankingBoard.setAdapter(rrItemAdapter);
        rrItemAdapter.notifyDataSetChanged();

        // unhighlight the previous ordering base-on
        if (previousStatus == RankingBoardOrderingStatus.PLACE_ASC || previousStatus == RankingBoardOrderingStatus.PLACE_DESC)
            txtvRankingBoardTitlePlace.setTextColor(getResources().getColor(R.color.color_general_ranking_board_title_text));
        else if (previousStatus == RankingBoardOrderingStatus.COST_TIME_ASC || previousStatus == RankingBoardOrderingStatus.COST_TIME_DESC)
            txtvRankingBoardTitleCostTime.setTextColor(getResources().getColor(R.color.color_general_ranking_board_title_text));
        else if (previousStatus == RankingBoardOrderingStatus.MOVED_COUNT_ASC || previousStatus == RankingBoardOrderingStatus.MOVED_COUNT_DESC)
            txtvRankingBoardTitleMovedCount.setTextColor(getResources().getColor(R.color.color_general_ranking_board_title_text));
        else
            txtvRankingBoardTitleDate.setTextColor(getResources().getColor(R.color.color_general_ranking_board_title_text));

        // highlight the current ordering base-on
        if (orderStatus == RankingBoardOrderingStatus.PLACE_ASC || orderStatus == RankingBoardOrderingStatus.PLACE_DESC)
            txtvRankingBoardTitlePlace.setTextColor(getResources().getColor(R.color.color_ordered_ranking_board_title_text));
        else if (orderStatus == RankingBoardOrderingStatus.COST_TIME_ASC || orderStatus == RankingBoardOrderingStatus.COST_TIME_DESC)
            txtvRankingBoardTitleCostTime.setTextColor(getResources().getColor(R.color.color_ordered_ranking_board_title_text));
        else if (orderStatus == RankingBoardOrderingStatus.MOVED_COUNT_ASC || orderStatus == RankingBoardOrderingStatus.MOVED_COUNT_DESC)
            txtvRankingBoardTitleMovedCount.setTextColor(getResources().getColor(R.color.color_ordered_ranking_board_title_text));
        else
            txtvRankingBoardTitleDate.setTextColor(getResources().getColor(R.color.color_ordered_ranking_board_title_text));
    };
}
