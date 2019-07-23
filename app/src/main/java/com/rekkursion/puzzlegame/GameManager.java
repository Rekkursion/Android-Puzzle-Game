package com.rekkursion.puzzlegame;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class GameManager {
    public enum Direction {
        UP, RIGHT, DOWN, LEFT
    }

    public enum GameStatus {
        PRE, GAMING, POST
    }

    public enum TimerStatus {
        RUNNING, PAUSED, STOPPED
    }

    public static final long IMAGE_TOO_BIG_WARNING_THRESHOLD = 1300L * 1300L;

    private static final GameManager instance = new GameManager();

    public Bitmap originalImageBitmap;
    public Bitmap scaledImageBitmap;
    public int difficulty;
    public GamingModeEnum gamingMode;
    public int[][] tagNumbersMap;
    public String selectedScaleTypeString;
    private Map<Integer, Integer> tagAndIdDict;
    private List<Object> UIList;
    public int movedCount;
    public GameStatus gameStatus;

    private TextView txtvMillisecondTimer;
    private Timer puzzlePlayingTimer;
    private long puzzlePlayingCounter_ms;
    public TimerStatus puzzerPlayingTimerStatus;

    private List<RankingRecordItemModel> rankingRecordItemList;

    public List<RankingRecordItemModel> getRankingRecordItemListFilteredByDifficulty(final int difficulty) {
        return rankingRecordItemList
                .stream()
                .filter(item -> item.getGameDifficulty() == difficulty)
                .collect(Collectors.toList());
    }

//    public List<RankingRecordItemModel> getRankingRecordItemList() {
//        return rankingRecordItemList;
//    }

    public void clearRankingRecordItemList() {
        if (rankingRecordItemList != null)
            rankingRecordItemList.clear();
        rankingRecordItemList = null;
    }

    public void addRankingRecordItem(RankingRecordItemModel item) {
        if (rankingRecordItemList == null)
            rankingRecordItemList = new ArrayList<>();
        rankingRecordItemList.add(item);
    }

    public void initPuzzlePlayingTimerAndSetTask(final TextView txtvMillisecondTimer) {
        puzzerPlayingTimerStatus = TimerStatus.RUNNING;

        if (this.txtvMillisecondTimer != null)
            this.txtvMillisecondTimer = null;
        this.txtvMillisecondTimer = txtvMillisecondTimer;

        if (puzzlePlayingTimer != null) {
            puzzlePlayingTimer.cancel();
            puzzlePlayingTimer = null;
        }
        puzzlePlayingTimer = new Timer();
        puzzlePlayingCounter_ms = 0L;

        puzzlePlayingTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (puzzerPlayingTimerStatus == TimerStatus.RUNNING)
                    puzzlePlayingCounter_ms += 1L;
                doActionHandler.sendEmptyMessage(1);
            }

            private Handler doActionHandler = new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);

                    long beforeDot = puzzlePlayingCounter_ms / 100L;
                    long afterDot = puzzlePlayingCounter_ms % 100L;
                    txtvMillisecondTimer.setText(String.format("%d.%02d", beforeDot, afterDot));
                }
            };
        }, 0L, 10L);
    }

    public void addUIWhichShouldBeDiscoveredWhenProcessingImage(Object obj) {
        if (UIList == null)
            UIList = new ArrayList<>();
        UIList.add(obj);
    }

    public void setVisibilitiesOfUIs(int visibility) {
        for (Object obj : UIList) {
            try {
                ((View) obj).setVisibility(visibility);
            } catch (Exception e) {}
        }
    }

    public void addTagAndIdEntry(int newTag, int newId) {
        if (tagAndIdDict == null)
            tagAndIdDict = new HashMap<>();
        tagAndIdDict.putIfAbsent(newTag, newId);
    }

    public void clearTagAndIdDict() {
        if (tagAndIdDict != null)
            tagAndIdDict.clear();
        tagAndIdDict = null;
    }

    public void clearUIList() {
        if (UIList != null)
            UIList.clear();
    }

    public int getImageViewIdByTag(int tag) {
        return tagAndIdDict.getOrDefault(tag, -1);
    }

    public int getAbandonedTagNumber() {
        return difficulty * difficulty - 1;
    }

    public void shuffle() {
        if (tagNumbersMap != null) {
            for (int r = 0; r < tagNumbersMap.length; ++r)
                tagNumbersMap[r] = null;
            tagNumbersMap = null;
        }

        tagNumbersMap = new int[difficulty][difficulty];
        for (int r = 0; r < difficulty; ++r) {
            for (int c = 0; c < difficulty; ++c)
                tagNumbersMap[r][c] = r * difficulty + c;
        }

        int tmpNum = tagNumbersMap[difficulty - 2][difficulty - 1];
        tagNumbersMap[difficulty - 2][difficulty - 1] = tagNumbersMap[difficulty - 1][difficulty - 1];
        tagNumbersMap[difficulty - 1][difficulty - 1] = tmpNum;
        return;

//        for (int r = 0; r < difficulty; ++r) {
//            for (int c = 0; c < difficulty; ++c) {
//                int _r = (int) Math.floor(Math.random() * difficulty);
//                int _c = (int) Math.floor(Math.random() * difficulty);
//
//                if (_r >= difficulty) _r = difficulty - 1;
//                if (_c >= difficulty) _c = difficulty - 1;
//
//                int tmp = tagNumbersMap[r][c];
//                tagNumbersMap[r][c] = tagNumbersMap[_r][_c];
//                tagNumbersMap[_r][_c] = tmp;
//            }
//        }
    }

    public boolean swapImageViews(ImageView view_a, ImageView view_b) {
        int tag_a = (int) view_a.getTag(), tag_b = (int) view_b.getTag();
        int id_a = view_a.getId(), id_b = view_b.getId();
        Bitmap bitmap_a = ((BitmapDrawable) view_a.getDrawable()).getBitmap(), bitmap_b = ((BitmapDrawable) view_b.getDrawable()).getBitmap();

        view_a.setTag(tag_b); view_a.setId(id_b); view_a.setImageBitmap(bitmap_b);
        view_b.setTag(tag_a); view_b.setId(id_a); view_b.setImageBitmap(bitmap_a);

        if (view_a.getVisibility() == View.INVISIBLE) {
            view_a.setVisibility(View.VISIBLE);
            view_b.setVisibility(View.INVISIBLE);
        } else if (view_b.getVisibility() == View.INVISIBLE) {
            view_a.setVisibility(View.INVISIBLE);
            view_b.setVisibility(View.VISIBLE);
        }

        swapTagNumbers(tag_a, tag_b);

        return hasFinished();
    }

    public boolean hasFinished() {
        for (int r = 0; r < difficulty; ++r) {
            for (int c = 0; c < difficulty; ++c) {
                int tag = r * difficulty + c;
                if (tagNumbersMap[r][c] != tag)
                    return false;
            }
        }
        return true;
    }

    public boolean isAbandonedTagNumber(int tag) {
        return tag == difficulty * difficulty - 1;
    }

    public int getTagAroundTheCertainTag(int certainTag, Direction dir) {
        for (int r = 0; r < difficulty; ++r) {
            for (int c = 0; c < difficulty; ++c) {
                if (tagNumbersMap[r][c] == certainTag) {
                    switch (dir) {
                        case UP: return r == 0 ? -1 : tagNumbersMap[r - 1][c];
                        case RIGHT: return c == difficulty - 1 ? -1 : tagNumbersMap[r][c + 1];
                        case DOWN: return r == difficulty - 1 ? -1 : tagNumbersMap[r + 1][c];
                        case LEFT: return c == 0 ? -1 : tagNumbersMap[r][c - 1];
                    }
                    return -1;
                }
            }
        }
        return -1;
    }

    public static GameManager getInstance() { return instance; }

    private void swapTagNumbers(int tag_a, int tag_b) {
        int _r = -1, _c = -1;
        for (int r = 0; r < difficulty; ++r) {
            for (int c = 0; c < difficulty; ++c) {
                if (tagNumbersMap[r][c] == tag_a || tagNumbersMap[r][c] == tag_b) {
                    if (_r < 0)
                    { _r = r; _c = c; }
                    else {
                        int tmp = tagNumbersMap[r][c];
                        tagNumbersMap[r][c] = tagNumbersMap[_r][_c];
                        tagNumbersMap[_r][_c] = tmp;
                        return;
                    }
                }
            }
        }
    }

    private GameManager() {}
}
