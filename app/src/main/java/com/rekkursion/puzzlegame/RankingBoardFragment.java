package com.rekkursion.puzzlegame;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class RankingBoardFragment extends Fragment {
    private RecyclerView recvRankingBoard;
    private List<RankingRecordItemModel> rankingRecordItemList;

    public RankingBoardFragment(List<RankingRecordItemModel> list) {
        super();
        this.rankingRecordItemList = list;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_ranking_board, container, false);
        recvRankingBoard = root.findViewById(R.id.recv_ranking_board);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager llyManager = new LinearLayoutManager(RankingBoardFragment.this.getContext());
        llyManager.setOrientation(RecyclerView.VERTICAL);

        recvRankingBoard.setLayoutManager(llyManager);

        RankingRecordItemAdapter rrItemAdapter = new RankingRecordItemAdapter(this.rankingRecordItemList);
        recvRankingBoard.setAdapter(rrItemAdapter);
        rrItemAdapter.notifyDataSetChanged();
    }
}
