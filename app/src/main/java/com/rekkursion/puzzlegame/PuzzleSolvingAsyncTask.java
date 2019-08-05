package com.rekkursion.puzzlegame;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

public class PuzzleSolvingAsyncTask extends AsyncTask<Void, Void, Void> {
    private ImageView[][] imageViews;
    private List<Integer> reversedMoveLogList;

    private ImageView currentView;
    private ImageView nextView;

    public void execute(ImageView[][] imgvs) {
        imageViews = imgvs;
        reversedMoveLogList = GameManager.getInstance().getMoveLogListCopied();
        Collections.reverse(reversedMoveLogList);

        super.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        reversedMoveLogList.forEach(integer -> {
            int row = integer / GameManager.getInstance().difficulty;
            int col = integer % GameManager.getInstance().difficulty;

            int currentViewTag = (int) imageViews[row][col].getTag();

            int upTag = GameManager.getInstance().getTagAroundTheCertainTag(currentViewTag, GameManager.Direction.UP);
            int rightTag = GameManager.getInstance().getTagAroundTheCertainTag(currentViewTag, GameManager.Direction.RIGHT);
            int downTag = GameManager.getInstance().getTagAroundTheCertainTag(currentViewTag, GameManager.Direction.DOWN);
            int leftTag = GameManager.getInstance().getTagAroundTheCertainTag(currentViewTag, GameManager.Direction.LEFT);

            currentView = imageViews[row][col];
            if (GameManager.getInstance().isAbandonedTagNumber(upTag))
                nextView = imageViews[row - 1][col];
            else if (GameManager.getInstance().isAbandonedTagNumber(rightTag))
                nextView = imageViews[row][col + 1];
            else if (GameManager.getInstance().isAbandonedTagNumber(downTag))
                nextView = imageViews[row + 1][col];
            else if (GameManager.getInstance().isAbandonedTagNumber(leftTag))
                nextView = imageViews[row][col - 1];

            doActionHandler.sendEmptyMessage(1);

            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {}
        });

        return null;
    }

    private Handler doActionHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            GameManager.getInstance().swapImageViews(currentView, nextView);
        }
    };
}
