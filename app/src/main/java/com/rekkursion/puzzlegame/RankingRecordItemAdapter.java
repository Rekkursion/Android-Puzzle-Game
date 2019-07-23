package com.rekkursion.puzzlegame;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RankingRecordItemAdapter extends RecyclerView.Adapter<RankingRecordItemAdapter.ViewHolder> {
    private List<RankingRecordItemModel> rankingRecordItemList;

    // constructor
    public RankingRecordItemAdapter(List<RankingRecordItemModel> list) {
        this.rankingRecordItemList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking_record, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //int rankingPlace = rankingRecordItemList.get(position).get
        int costTime = rankingRecordItemList.get(position).getCostTime();
        int movedCount = rankingRecordItemList.get(position).getMovedCount();
        Date recordDoneDate = rankingRecordItemList.get(position).getRecordDate();

        holder.setData(position + 1, costTime, movedCount, recordDoneDate);
    }

    @Override
    public int getItemCount() {
        return rankingRecordItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtvRRItemRankingPlace;
        private TextView txtvRRItemCostTime;
        private TextView txtvRRItemMovedCount;
        private TextView txtvRRItemRecordDoneDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtvRRItemRankingPlace = itemView.findViewById(R.id.txtv_rr_item_ranking_place);
            txtvRRItemCostTime = itemView.findViewById(R.id.txtv_rr_item_cost_time);
            txtvRRItemMovedCount = itemView.findViewById(R.id.txtv_rr_item_moved_count);
            txtvRRItemRecordDoneDate = itemView.findViewById(R.id.txtv_rr_item_record_done_date);

            // not showing txtv-rr-item-ranking-place since i have no idea how to deal with ranking places
            txtvRRItemRankingPlace.setVisibility(View.GONE);
        }

        private void setData(int rankingPlace, int costTime, int movedCount, Date recordDoneDate) {
            txtvRRItemRankingPlace.setText(String.valueOf(rankingPlace));

            String costTimeString = String.format("%d.%02d", costTime / 100, costTime % 100);
            txtvRRItemCostTime.setText(costTimeString);

            txtvRRItemMovedCount.setText(String.valueOf(movedCount));
            txtvRRItemRecordDoneDate.setText(new SimpleDateFormat(GameManager.RECORD_DATE_AND_TIME_FORMAT_STRING).format(recordDoneDate));
        }
    }
}
