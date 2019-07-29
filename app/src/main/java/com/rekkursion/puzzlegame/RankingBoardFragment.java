package com.rekkursion.puzzlegame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    private LinearLayout llyRankingBoardTitleCostTimeContainer;
    private LinearLayout llyRankingBoardTitleMovedCountContainer;
    private LinearLayout llyRankingBoardTitleDateContainer;

    private TextView txtvRankingBoardTitlePlace;
    private TextView txtvRankingBoardTitleCostTime;
    private TextView txtvRankingBoardTitleMovedCount;
    private TextView txtvRankingBoardTitleDate;

    private ImageView imgvRankingBoardTitleCostTimeOrderingIcon;
    private ImageView imgvRankingBoardTitleMovedCountOrderingIcon;
    private ImageView imgvRankingBoardTitleDateOrderingIcon;

    private RankingBoardOrderingStatus orderStatus;

    private RankingRecordItemModel newRecordItemModel;

    private SQLiteDatabaseHelper sqlHelper;

    public RankingBoardFragment(List<RankingRecordItemModel> list, SQLiteDatabaseHelper sqlHelper) {
        super();
        this.rankingRecordItemList = list;
        this.sqlHelper = sqlHelper;
        newRecordItemModel = null;
    }

    public RankingBoardFragment(List<RankingRecordItemModel> list, SQLiteDatabaseHelper sqlHelper, RankingRecordItemModel newRecordItemModel) {
        super();
        this.rankingRecordItemList = list;
        this.sqlHelper = sqlHelper;
        this.newRecordItemModel = newRecordItemModel;
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

        imgvRankingBoardTitleCostTimeOrderingIcon = root.findViewById(R.id.imgv_ranking_board_title_cost_time_ordering_icon);
        imgvRankingBoardTitleMovedCountOrderingIcon = root.findViewById(R.id.imgv_ranking_board_title_moved_count_ordering_icon);
        imgvRankingBoardTitleDateOrderingIcon = root.findViewById(R.id.imgv_ranking_board_title_record_done_date_ordering_icon);

        llyRankingBoardTitleCostTimeContainer = root.findViewById(R.id.lly_ranking_board_title_cost_time_container);
        llyRankingBoardTitleMovedCountContainer = root.findViewById(R.id.lly_ranking_board_title_moved_count_container);
        llyRankingBoardTitleDateContainer = root.findViewById(R.id.lly_ranking_board_title_record_done_date_container);

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

        // set adapter and add item-decoration for recycler-view
        int idxOfNewRecord = newRecordItemModel == null ? -1 : this.rankingRecordItemList.indexOf(newRecordItemModel);
        setRecvAdapterAndItemDecorationThroughDataList(this.rankingRecordItemList, idxOfNewRecord);

        // set on-touch-listener for recycler-view
        setRecyclerViewOnItemTouchListener();

        // set default status of ranking board ordering
        orderStatus = RankingBoardOrderingStatus.COST_TIME_ASC;

        // set on-click-listener for titles of recycler-view
        // txtvRankingBoardTitlePlace.setOnClickListener(rankingBoardTitlesOnClickListener);
        llyRankingBoardTitleCostTimeContainer.setOnClickListener(rankingBoardTitlesOnClickListener);
        llyRankingBoardTitleMovedCountContainer.setOnClickListener(rankingBoardTitlesOnClickListener);
        llyRankingBoardTitleDateContainer.setOnClickListener(rankingBoardTitlesOnClickListener);

        // not showing txtv-ranking-board-title-place since i have no idea how to deal with ranking places
        txtvRankingBoardTitlePlace.setVisibility(View.GONE);
    }

    private void setRecyclerViewOnItemTouchListener() {
        recvRankingBoard.addOnItemTouchListener(new RecyclerViewOnItemTouchListener(recvRankingBoard, new RecyclerViewOnItemTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SoundPoolManager.getInstance().play("se_maoudamashii_click.mp3");
            }

            @Override
            public void onItemLongClick(View view, int position) {
                // vibrate
                try {
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(VibrationEffect.createOneShot(60L, VibrationEffect.DEFAULT_AMPLITUDE));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // show the dialog before the data deletion
                new AlertDialog.Builder(RankingBoardFragment.this.getContext())
                        .setIcon(R.drawable.ic_warning_orange_24dp)
                        .setMessage(R.string.str_user_check_before_delete_the_record)
                        .setPositiveButton(R.string.str_user_check_yes, (dialogInterface, i) -> {
                            // delete data at sql
                            sqlHelper.deleteData(rankingRecordItemList.get(position));

                            // delete data at list
                            rankingRecordItemList.remove(position);
                            int idxOfNewRecord = newRecordItemModel == null ? -1 : rankingRecordItemList.indexOf(newRecordItemModel);
                            setRecvAdapterAndItemDecorationThroughDataList(rankingRecordItemList, idxOfNewRecord);

                            // toast to tell user that the data has already been deleted
                            Toast.makeText(RankingBoardFragment.this.getContext(), "Deleted successfully.", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(R.string.str_user_check_no, null)
                        .show();
            }
        }));
    }

    // ranking board titles click events: ordering features
    private View.OnClickListener rankingBoardTitlesOnClickListener = view -> {
        RankingBoardOrderingStatus previousStatus = orderStatus;

        switch (view.getId()) {
            // never enter
            case R.id.txtv_ranking_board_title_place:
                if (orderStatus == RankingBoardOrderingStatus.PLACE_ASC) {
                    this.rankingRecordItemList = this.rankingRecordItemList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                    orderStatus = RankingBoardOrderingStatus.PLACE_DESC;
                } else {
                    this.rankingRecordItemList = this.rankingRecordItemList.stream().sorted().collect(Collectors.toList());
                    orderStatus = RankingBoardOrderingStatus.PLACE_ASC;
                }
                break;

            case R.id.lly_ranking_board_title_cost_time_container:
                if (orderStatus == RankingBoardOrderingStatus.COST_TIME_ASC) {
                    this.rankingRecordItemList = this.rankingRecordItemList.stream().sorted(Comparator.comparing(RankingRecordItemModel::getCostTime).reversed()).collect(Collectors.toList());
                    orderStatus = RankingBoardOrderingStatus.COST_TIME_DESC;
                } else {
                    this.rankingRecordItemList = this.rankingRecordItemList.stream().sorted(Comparator.comparing(RankingRecordItemModel::getCostTime)).collect(Collectors.toList());
                    orderStatus = RankingBoardOrderingStatus.COST_TIME_ASC;
                }
                break;

            case R.id.lly_ranking_board_title_moved_count_container:
                if (orderStatus == RankingBoardOrderingStatus.MOVED_COUNT_ASC) {
                    this.rankingRecordItemList = this.rankingRecordItemList.stream().sorted(Comparator.comparing(RankingRecordItemModel::getMovedCount).reversed()).collect(Collectors.toList());
                    orderStatus = RankingBoardOrderingStatus.MOVED_COUNT_DESC;
                } else {
                    this.rankingRecordItemList = this.rankingRecordItemList.stream().sorted(Comparator.comparing(RankingRecordItemModel::getMovedCount)).collect(Collectors.toList());
                    orderStatus = RankingBoardOrderingStatus.MOVED_COUNT_ASC;
                }
                break;

            case R.id.lly_ranking_board_title_record_done_date_container:
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
                                return ld.before(rd) ? -1 : 1;
                            })
                            .collect(Collectors.toList());
                    orderStatus = RankingBoardOrderingStatus.DATE_ASC;
                }
                break;
        }

        // reset adapter and item-decoration
        int idxOfNewRecord = newRecordItemModel == null ? -1 : this.rankingRecordItemList.indexOf(newRecordItemModel);
        setRecvAdapterAndItemDecorationThroughDataList(this.rankingRecordItemList, idxOfNewRecord);

        // unhighlight the previous ordering base-on
        if (previousStatus == RankingBoardOrderingStatus.PLACE_ASC || previousStatus == RankingBoardOrderingStatus.PLACE_DESC)
            txtvRankingBoardTitlePlace.setTextColor(getResources().getColor(R.color.color_general_ranking_board_title_text));
        else if (previousStatus == RankingBoardOrderingStatus.COST_TIME_ASC || previousStatus == RankingBoardOrderingStatus.COST_TIME_DESC) {
            txtvRankingBoardTitleCostTime.setTextColor(getResources().getColor(R.color.color_general_ranking_board_title_text));
            imgvRankingBoardTitleCostTimeOrderingIcon.setVisibility(View.GONE);
        } else if (previousStatus == RankingBoardOrderingStatus.MOVED_COUNT_ASC || previousStatus == RankingBoardOrderingStatus.MOVED_COUNT_DESC) {
            txtvRankingBoardTitleMovedCount.setTextColor(getResources().getColor(R.color.color_general_ranking_board_title_text));
            imgvRankingBoardTitleMovedCountOrderingIcon.setVisibility(View.GONE);
        } else {
            txtvRankingBoardTitleDate.setTextColor(getResources().getColor(R.color.color_general_ranking_board_title_text));
            imgvRankingBoardTitleDateOrderingIcon.setVisibility(View.GONE);
        }

        // highlight the current ordering base-on
        if (orderStatus == RankingBoardOrderingStatus.PLACE_ASC || orderStatus == RankingBoardOrderingStatus.PLACE_DESC)
            txtvRankingBoardTitlePlace.setTextColor(getResources().getColor(R.color.color_ordered_ranking_board_title_text));
        else if (orderStatus == RankingBoardOrderingStatus.COST_TIME_ASC || orderStatus == RankingBoardOrderingStatus.COST_TIME_DESC) {
            txtvRankingBoardTitleCostTime.setTextColor(getResources().getColor(R.color.color_ordered_ranking_board_title_text));
            imgvRankingBoardTitleCostTimeOrderingIcon.setVisibility(View.VISIBLE);
            imgvRankingBoardTitleCostTimeOrderingIcon.setImageResource(orderStatus == RankingBoardOrderingStatus.COST_TIME_ASC ? R.drawable.ic_arrow_drop_up_orange_24dp : R.drawable.ic_arrow_drop_down_orange_24dp);
        } else if (orderStatus == RankingBoardOrderingStatus.MOVED_COUNT_ASC || orderStatus == RankingBoardOrderingStatus.MOVED_COUNT_DESC) {
            txtvRankingBoardTitleMovedCount.setTextColor(getResources().getColor(R.color.color_ordered_ranking_board_title_text));
            imgvRankingBoardTitleMovedCountOrderingIcon.setVisibility(View.VISIBLE);
            imgvRankingBoardTitleMovedCountOrderingIcon.setImageResource(orderStatus == RankingBoardOrderingStatus.MOVED_COUNT_ASC ? R.drawable.ic_arrow_drop_up_orange_24dp : R.drawable.ic_arrow_drop_down_orange_24dp);
        } else {
            txtvRankingBoardTitleDate.setTextColor(getResources().getColor(R.color.color_ordered_ranking_board_title_text));
            imgvRankingBoardTitleDateOrderingIcon.setVisibility(View.VISIBLE);
            imgvRankingBoardTitleDateOrderingIcon.setImageResource(orderStatus == RankingBoardOrderingStatus.DATE_ASC ? R.drawable.ic_arrow_drop_up_orange_24dp : R.drawable.ic_arrow_drop_down_orange_24dp);
        }

        SoundPoolManager.getInstance().play("se_maoudamashii_ranking_board_reordering.mp3");
    };

    private void setRecvAdapterAndItemDecorationThroughDataList(List<RankingRecordItemModel> dataList, int newRecordIndex) {
        // reset item-adapter for recycler-view
        RankingRecordItemAdapter rrItemAdapter = new RankingRecordItemAdapter(dataList);
        // recvRankingBoard.swapAdapter(rrItemAdapter, true);
        recvRankingBoard.setAdapter(rrItemAdapter);
        rrItemAdapter.notifyDataSetChanged();

        for (int k = 0; k < recvRankingBoard.getItemDecorationCount(); ++k)
            recvRankingBoard.removeItemDecorationAt(k);
        recvRankingBoard.addItemDecoration(new RankingRecordItemDecoration(RankingBoardFragment.this.getContext(), newRecordIndex));
    }
}
