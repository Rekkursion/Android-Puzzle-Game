package com.rekkursion.puzzlegame;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Collections;
import java.util.List;

public class PuzzleSolvingAsyncTask extends AsyncTask<Void, Void, Void> {
    private static final int AUTO_SOLVING_NOTIFICATION_ID = 8620;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    private ImageView[][] imageViews;
    private List<Integer> reversedMoveLogList;

    private ImageView currentView;
    private ImageView nextView;

    private TextView txtvGoBackWhenAutoSolvingHasFinished;

    public void execute(Context context, ImageView[][] imgvs, TextView txtvGoBackWhenAutoSolvingHasFinished) {
        this.txtvGoBackWhenAutoSolvingHasFinished = txtvGoBackWhenAutoSolvingHasFinished;
        imageViews = imgvs;

        reversedMoveLogList = GameManager.getInstance().getMoveLogListCopied();
        Collections.reverse(reversedMoveLogList);

        // notification
        try {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationBuilder = new NotificationCompat.Builder(context, context.getPackageName())
                    .setSmallIcon(R.drawable.ic_blocks_orange_24dp)
                    .setContentTitle("Auto solving your puzzle...")
                    .setContentText("")
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(context.getPackageName(), "TAG", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            notificationBuilder.setChannelId(context.getPackageName());

            notificationManager.notify(AUTO_SOLVING_NOTIFICATION_ID, notificationBuilder.build());
        } catch (Exception e) {
            if (notificationManager != null) {
                notificationManager.cancelAll();
                notificationManager = null;
            }
        }

        super.execute();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        for (int k = 0; k < reversedMoveLogList.size(); ++k) {
            int integer = reversedMoveLogList.get(k);

            notificationBuilder.setProgress(reversedMoveLogList.size(), k, false);
            notificationManager.notify(AUTO_SOLVING_NOTIFICATION_ID, notificationBuilder.build());

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
        }
        reversedMoveLogList.forEach(integer -> {

        });

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        GameManager.getInstance().finishTheGame(imageViews[GameManager.getInstance().difficulty - 1][GameManager.getInstance().difficulty - 1], imageViews);
        txtvGoBackWhenAutoSolvingHasFinished.setVisibility(View.VISIBLE);

        if (notificationManager != null) {
            notificationManager.cancel(AUTO_SOLVING_NOTIFICATION_ID);
            notificationManager = null;
        }
    }

    private Handler doActionHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            GameManager.getInstance().swapImageViews(currentView, nextView);
        }
    };
}
