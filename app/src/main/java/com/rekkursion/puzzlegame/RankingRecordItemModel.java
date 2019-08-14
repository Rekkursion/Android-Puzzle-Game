package com.rekkursion.puzzlegame;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RankingRecordItemModel implements Comparable<RankingRecordItemModel>, Serializable {
    private int gameDifficulty;
    private int movedCount;
    private int costTime;
    private Date recordDate;
    private String scaledBitmapFilename;

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

    public String getRecordDateStringByFormat(String format) {
        return new SimpleDateFormat(format).format(recordDate);
    }

    public String getScaledBitmapFilename() {
        return scaledBitmapFilename;
    }

//    public RankingRecordItemModel(int gameDifficulty, int movedCount, int costTime, String scaledBitmapFilename) {
//        this(gameDifficulty, movedCount, costTime, scaledBitmapFilename, null);
//    }

    public RankingRecordItemModel(int gameDifficulty, int movedCount, int costTime, String scaledBitmapFilename, Date recordDate) {
        this.gameDifficulty = gameDifficulty;
        this.movedCount = movedCount;
        this.costTime = costTime;
        this.scaledBitmapFilename = scaledBitmapFilename;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RankingRecordItemModel))
            return false;

        RankingRecordItemModel rhs = (RankingRecordItemModel) obj;
        return this.gameDifficulty == rhs.gameDifficulty &&
                this.costTime == rhs.costTime &&
                this.movedCount == rhs.movedCount &&
                this.recordDate.toString().equals(rhs.recordDate.toString());
    }
}
