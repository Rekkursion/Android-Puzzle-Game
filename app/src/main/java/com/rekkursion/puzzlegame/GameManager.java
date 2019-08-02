package com.rekkursion.puzzlegame;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import java.util.Random;
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
    public static final String RECORD_DATE_AND_TIME_FORMAT_STRING = "yyyy/MM/dd HH:mm:ss";

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
    public boolean isShowingIndices;

    private Bitmap[][] bitmapBlocksArray;
    private Bitmap[][] bitmapBlocksWithIndicesArray;

    private TextView txtvMillisecondTimer;
    private Timer puzzlePlayingTimer;
    private long puzzlePlayingCounter_ms;
    public TimerStatus puzzerPlayingTimerStatus;

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
        final int SHUFFLE_TIMES = 100 * difficulty;

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

//        fake shuffling
//        int tmpNum = tagNumbersMap[difficulty - 2][difficulty - 1];
//        tagNumbersMap[difficulty - 2][difficulty - 1] = tagNumbersMap[difficulty - 1][difficulty - 1];
//        tagNumbersMap[difficulty - 1][difficulty - 1] = tmpNum;
//        return;

        int currentBlankRow = getAbandonedTagNumber() / difficulty;
        int currentBlankCol = getAbandonedTagNumber() % difficulty;
        Random random = new Random();
        for (int k = 0; k < SHUFFLE_TIMES; ++k) {
            int nextBlankRow, nextBlankCol;
            int moveDirection = random.nextInt(4);

            switch (moveDirection) {
                // up
                case 0: nextBlankRow = currentBlankRow - 1; nextBlankCol = currentBlankCol; break;
                // right
                case 1: nextBlankRow = currentBlankRow; nextBlankCol = currentBlankCol + 1; break;
                // down
                case 2: nextBlankRow = currentBlankRow + 1; nextBlankCol = currentBlankCol; break;
                // left
                case 3: nextBlankRow = currentBlankRow; nextBlankCol = currentBlankCol - 1; break;
                // exception
                default: nextBlankRow = currentBlankRow; nextBlankCol = currentBlankCol;
            }

            // the next position is valid (not out of range and not the same position as current one), swap
            if (nextBlankRow >= 0 && nextBlankRow < difficulty && nextBlankCol >= 0 && nextBlankCol < difficulty &&
                    (nextBlankRow != currentBlankRow || nextBlankCol != currentBlankCol)) {
                int tmp = tagNumbersMap[currentBlankRow][currentBlankCol];
                tagNumbersMap[currentBlankRow][currentBlankCol] = tagNumbersMap[nextBlankRow][nextBlankCol];
                tagNumbersMap[nextBlankRow][nextBlankCol] = tmp;

                currentBlankRow = nextBlankRow;
                currentBlankCol = nextBlankCol;
            }
        }

//        wrong shuffling
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

    public void setImageViewBlocksArray(ImageView[][] arr) {
        if (bitmapBlocksArray != null)
            bitmapBlocksArray = null;
        if (bitmapBlocksWithIndicesArray != null)
            bitmapBlocksWithIndicesArray = null;

        bitmapBlocksArray = new Bitmap[difficulty][difficulty];
        bitmapBlocksWithIndicesArray = new Bitmap[difficulty][difficulty];

        for (int r = 0; r < difficulty; ++r) {
            for (int c = 0; c < difficulty; ++c) {
                Bitmap origBitmap = ((BitmapDrawable) arr[r][c].getDrawable()).getBitmap();
                int idx = (int) arr[r][c].getTag();

                // draw indices on the original bitmap
                Bitmap withIdxBitmap = Bitmap.createBitmap(origBitmap);
                Canvas canvas = new Canvas(withIdxBitmap);
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setTextSize(31.62F);
                String idxString = GameManager.getInstance().difficulty == 3 ? String.valueOf(idx + 1) : String.format("%02d", idx + 1);
                canvas.drawText(idxString, 4.0F, 25.0F, paint);

                // set image bitmaps
                bitmapBlocksArray[r][c] = Bitmap.createBitmap(origBitmap);
                bitmapBlocksWithIndicesArray[r][c] = Bitmap.createBitmap(withIdxBitmap);
            }
        }
    }

    public void showOrHideIndices(ImageView[][] imgvs) {
        if (bitmapBlocksWithIndicesArray == null)
            return;

        for(int r = 0; r < difficulty; ++r) {
            for (int c = 0; c < difficulty; ++c) {
                // show -> hide
                if (isShowingIndices)
                    imgvs[r][c].setImageBitmap(bitmapBlocksArray[r][c]);
                // hide -> show
                else
                    imgvs[r][c].setImageBitmap(bitmapBlocksWithIndicesArray[r][c]);
            }
        }

        isShowingIndices = !isShowingIndices;
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
