package com.rekkursion.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {
    public static Bitmap origImageBitmap;
    public static int selectedDifficulty;
    public static GamingModeEnum gamingMode;

    private ImageView imgvScaledImage;
    private ProgressBar pgbWaitForImageProcessing;
    private TextView txtvWaitForImageProcessing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        imgvScaledImage = findViewById(R.id.imgv_scaled_image);
        pgbWaitForImageProcessing = findViewById(R.id.pgb_wait_for_image_processing);
        txtvWaitForImageProcessing = findViewById(R.id.txtv_wait_for_image_processing);

        ImageScalingAsyncTask imageScalingAsyncTask = new ImageScalingAsyncTask();
        imageScalingAsyncTask.execute(origImageBitmap, imgvScaledImage, pgbWaitForImageProcessing, txtvWaitForImageProcessing);
    }
}
