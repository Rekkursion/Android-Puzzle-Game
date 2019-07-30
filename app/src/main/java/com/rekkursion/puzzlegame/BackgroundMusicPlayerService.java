package com.rekkursion.puzzlegame;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

// Source: https://blog.csdn.net/imyang2007/article/details/7597040
// Source: https://codertw.com/android-%E9%96%8B%E7%99%BC/352569/
public class BackgroundMusicPlayerService extends Service {
    public static int mainThemeSeekPosition = 0;

    private MediaPlayer mp;
    private boolean prepared;

    @Override
    public void onCreate() {
        // initialize
        prepared = false;
        try {
            AssetFileDescriptor afd = getAssets().openFd("musics" + File.separator + BackgroundMusicManager.getInstance().currentPlayingFilename);
            mp = new MediaPlayer();
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mp.prepareAsync();

            // when the music is completed
            mp.setOnCompletionListener(mediaPlayer -> {
                try {
                    // loop
                    mediaPlayer.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            });

            // when error happens
            mp.setOnErrorListener((mediaPlayer, what, extra) -> {
                try {
                    // release resources
                    mp.release();
                    // stop the service
                    stopSelf();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            });

            // when the media-player is prepared
            mp.setOnPreparedListener(mediaPlayer -> {
                prepared = true;
                mp.setLooping(true);
                mp.start();
                mp.seekTo(mainThemeSeekPosition);
            });
        } catch (IllegalStateException e) {
            prepared = false;
            e.printStackTrace();
        } catch (IOException e) {
            prepared = false;
            e.printStackTrace();
        }

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // start playing music
        if (prepared && !mp.isPlaying()) {
            mp.start();
            mp.seekTo(mainThemeSeekPosition);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // save the stopped position
        mainThemeSeekPosition = mp.getCurrentPosition();

        // stop and release resources
        if (mp.isPlaying())
            mp.stop();
        mp.release();

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
