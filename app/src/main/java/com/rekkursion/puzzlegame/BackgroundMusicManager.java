package com.rekkursion.puzzlegame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

// Source: https://blog.csdn.net/yu132563/article/details/77412859
public class BackgroundMusicManager {
    public static boolean shouldStopPlayingWhenLeaving = true;

    private static BackgroundMusicManager backgroundMusicManager = null;
    private static final String TAG = "Bg_Music";

    private float mLeftVolume;
    private float mRightVolume;
    private Context mContext;
    private MediaPlayer mBackgroundMediaPlayer;
    private boolean mIsPaused;
    private String mCurrentPath;

    private BackgroundMusicManager(Context context) {
        this.mContext = context;
        initData();
    }

    public static BackgroundMusicManager getInstance(Context context) {
        if (backgroundMusicManager == null)
            backgroundMusicManager = new BackgroundMusicManager(context);
        return backgroundMusicManager;
    }

    private void initData() {
        mLeftVolume = 1.0F;
        mRightVolume = 1.0F;
        mBackgroundMediaPlayer = null;
        mIsPaused = false;
        mCurrentPath = null;
    }

    public void play(String path, boolean isLoop) {
        if (mCurrentPath == null) {
            mBackgroundMediaPlayer = createMediaPlayerFromAssets(path);
            mCurrentPath = path;
        } else {
            if (!mCurrentPath.equals(path)) {
                if (mBackgroundMediaPlayer != null)
                    mBackgroundMediaPlayer.release();
                mBackgroundMediaPlayer = createMediaPlayerFromAssets(path);
                mCurrentPath = path;
            }
        }

        if (mBackgroundMediaPlayer == null) {
            Log.e(TAG, "playBackgroundMusic: background media player is null");
        } else {
            mBackgroundMediaPlayer.stop();
            mBackgroundMediaPlayer.setLooping(isLoop);
            try {
                mBackgroundMediaPlayer.prepare();
                mBackgroundMediaPlayer.seekTo(0);
                mBackgroundMediaPlayer.start();
                this.mIsPaused = false;
            } catch (Exception e) {
                Log.e(TAG, "playBackgroundMusic: error state");
            }
        }
    }

    public void stop() {
        if (mBackgroundMediaPlayer != null) {
            mBackgroundMediaPlayer.stop();
            this.mIsPaused = false;
        }
    }

    public void pause() {
        if (mBackgroundMediaPlayer != null && mBackgroundMediaPlayer.isPlaying()) {
            mBackgroundMediaPlayer.pause();
            this.mIsPaused = true;
        }
    }

    public void resume() {
        if (mBackgroundMediaPlayer != null && this.mIsPaused) {
            mBackgroundMediaPlayer.start();
            this.mIsPaused = false;
        }
    }

    public void rewind() {
        if (mBackgroundMediaPlayer != null) {
            mBackgroundMediaPlayer.stop();
            try {
                mBackgroundMediaPlayer.prepare();
                mBackgroundMediaPlayer.seekTo(0);
                mBackgroundMediaPlayer.start();
                this.mIsPaused = false;
            } catch (Exception e) {
                Log.e(TAG, "rewindBackgroundMusic: error state");
            }
        }
    }

    public boolean isPlaying() {
        boolean ret;
        if (mBackgroundMediaPlayer == null)
            ret = false;
        else
            ret = mBackgroundMediaPlayer.isPlaying();
        return ret;
    }

    public void endAndRelease() {
        if (mBackgroundMediaPlayer != null)
            mBackgroundMediaPlayer.release();
        initData();
    }

    public float getVolume() {
        if (this.mBackgroundMediaPlayer != null)
            return (this.mLeftVolume + this.mRightVolume) / 2.0F;
        else
            return 0.0F;
    }

    public void setVolume(float volume) {
        this.mLeftVolume = this.mRightVolume = volume;
        if (this.mBackgroundMediaPlayer != null)
            this.mBackgroundMediaPlayer.setVolume(this.mLeftVolume, this.mRightVolume);
    }

    private MediaPlayer createMediaPlayerFromAssets(String path) {
        MediaPlayer mediaPlayer = null;
        try {
            AssetFileDescriptor afd = mContext.getAssets().openFd(path);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.setVolume(mLeftVolume, mRightVolume);
        } catch (Exception e) {
            mediaPlayer = null;
            Log.e(TAG, "error: " + e.getMessage(), e);
        }

        return mediaPlayer;
    }
}
