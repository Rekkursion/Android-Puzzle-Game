package com.rekkursion.puzzlegame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private final int REQ_CODE_PERMISSION_VIBRATE = 128128;
    private boolean firstClicked = false;
    private boolean homing = true;

    private LinearLayout llyBodyAtMainActivity;
    private TextView txtvStartButtonAtMainActivity;
    private TextView txtvStartButtonShadowAtMainActivity;
    private ImageView imgvPuzzleGameTitleAtMainActivity;

    private Animation animScaleLittleWithBouncing;
    private Animation animScaleLargeWithOvershooting;
    private int puzzleGameTitleAnimationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // request the usage of vibration
        if (checkSelfPermission(Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[] { Manifest.permission.VIBRATE }, REQ_CODE_PERMISSION_VIBRATE);

        initAnimations();
        initViews();
        initSounds();

        // add the animation set to the button shadow
        txtvStartButtonShadowAtMainActivity.startAnimation(getShadowEffectAnimationSet(MainActivity.this));
    }

    @Override
    protected void onResume() {
        firstClicked = false;
        super.onResume();
    }

    @Override
    protected void onStart() {
        if (homing) {
            Intent intent = new Intent(this, BackgroundMusicPlayerService.class);
            startService(intent);
        }
        homing = true;

        super.onStart();
    }

    @Override
    protected void onStop() {
        if (homing) {
            Intent intent = new Intent(this, BackgroundMusicPlayerService.class);
            stopService(intent);
        }

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (!firstClicked) {
            Snackbar.make(llyBodyAtMainActivity, R.string.str_press_again_to_leave, Snackbar.LENGTH_SHORT).show();
            firstClicked = true;
        } else {
            super.onBackPressed();
        }
    }

    private void initSounds() {
        try {
            List<String> audioList = Arrays.stream(getAssets().list(SoundPoolManager.SOUND_FILES_ROOT_PATH)).collect(Collectors.toList());
            SoundPoolManager.getInstance().initSoundPool(MainActivity.this, audioList);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error happened when getting sound files.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        llyBodyAtMainActivity = findViewById(R.id.lly_body_at_main_activity);
        txtvStartButtonAtMainActivity = findViewById(R.id.txtv_start_button_at_main_activity);
        txtvStartButtonShadowAtMainActivity = findViewById(R.id.txtv_start_button_shadow_at_main_activity);
        imgvPuzzleGameTitleAtMainActivity = findViewById(R.id.imgv_puzzle_game_title_at_main_activity);

        txtvStartButtonAtMainActivity.setOnClickListener(view -> {
            Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_animation_view_pressed);
            anim.setFillAfter(false);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    goToMenuActivity();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            txtvStartButtonAtMainActivity.startAnimation(anim);

            SoundPoolManager.getInstance().play("se_maoudamashii_click_entering.mp3");
        });

        // set on-click-listener for animations
        imgvPuzzleGameTitleAtMainActivity.setOnClickListener(view -> {
            firstClicked = false;

            if (puzzleGameTitleAnimationStatus == 0) {
                imgvPuzzleGameTitleAtMainActivity.startAnimation(animScaleLittleWithBouncing);
                puzzleGameTitleAnimationStatus = 1;
                SoundPoolManager.getInstance().play("se_maoudamashii_onepoint31.mp3", 0, 1.2F);
            } else if (puzzleGameTitleAnimationStatus == 1) {
                imgvPuzzleGameTitleAtMainActivity.startAnimation(animScaleLargeWithOvershooting);
                puzzleGameTitleAnimationStatus = 0;
                SoundPoolManager.getInstance().play("se_maoudamashii_onepoint09.mp3", 0, 1.2F);
            }
        });
    }

    private void initAnimations() {
        puzzleGameTitleAnimationStatus = 0;

        animScaleLittleWithBouncing = new ScaleAnimation(1.0F, 0.5F, 1.0F, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        animScaleLittleWithBouncing.setDuration(1500L);
        animScaleLittleWithBouncing.setFillAfter(true);
        animScaleLittleWithBouncing.setInterpolator(new BounceInterpolator());

        animScaleLargeWithOvershooting = new ScaleAnimation(0.5F, 1.0F, 0.5F, 1.0F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        animScaleLargeWithOvershooting.setDuration(200L);
        animScaleLargeWithOvershooting.setFillAfter(true);
        animScaleLargeWithOvershooting.setInterpolator(new OvershootInterpolator());
    }

    public static AnimationSet getShadowEffectAnimationSet(Context context) {
        // create shadow animation set:

        // get shadow animations
        Animation scaleLargeAnim = AnimationUtils.loadAnimation(context, R.anim.scale_large_and_turn_init_immediately);
        Animation fadeOutAnim = AnimationUtils.loadAnimation(context, R.anim.fade_out_and_turn_init_immediately);

        // build the animation set
        AnimationSet animSetShadowEffect = new AnimationSet(true);
        animSetShadowEffect.addAnimation(scaleLargeAnim);
        animSetShadowEffect.addAnimation(fadeOutAnim);

        return animSetShadowEffect;
    }

    private void goToMenuActivity() {
        homing = false;
        Intent toMenuActivityIntent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(toMenuActivityIntent);
    }
}
