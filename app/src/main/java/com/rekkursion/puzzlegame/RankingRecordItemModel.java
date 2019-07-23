package com.rekkursion.puzzlegame;

import java.util.Calendar;
import java.util.Date;

public class RankingRecordItemModel implements Comparable<RankingRecordItemModel> {
    private int gameDifficulty;
    private int movedCount;
    private int costTime;
    private Date recordDate;

    public int getGameDifficulty() {
        return gameDifficulty;
    }

    public int getMovedCount() {
        return movedCount;
    }

    public int getCostTime() {
        return costTime;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public RankingRecordItemModel(int gameDifficulty, int movedCount, int costTime) {
        this(gameDifficulty, movedCount, costTime, null);
    }

    public RankingRecordItemModel(int gameDifficulty, int movedCount, int costTime, Date recordDate) {
        this.gameDifficulty = gameDifficulty;
        this.movedCount = movedCount;
        this.costTime = costTime;
        this.recordDate = recordDate == null ? Calendar.getInstance().getTime() : recordDate;
    }

    @Override
    public int compareTo(RankingRecordItemModel rhs) {
        if (gameDifficulty != rhs.gameDifficulty)
            return gameDifficulty - rhs.gameDifficulty;
        if (costTime != rhs.costTime)
            return costTime - rhs.costTime;
        if (movedCount != rhs.movedCount)
            return movedCount - rhs.movedCount;
        return recordDate.compareTo(rhs.recordDate);
    }
}
