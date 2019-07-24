package com.rekkursion.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private Button btnStartAtMenu;
    private Button btnStartShadowAtMenu;
    private ImageView imgvPuzzleGameTitleAtMainActivity;

    private Animation animScaleLittleWithBouncing;
    private Animation animScaleLargeWithOvershooting;
    private int puzzleGameTitleAnimationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAnimations();
        initViews();

        // add the animation set to the button shadow
        btnStartShadowAtMenu.startAnimation(getShadowEffectAnimationSet(MainActivity.this));
    }

    private void initViews() {
        btnStartAtMenu = findViewById(R.id.btn_start_at_menu);
        btnStartShadowAtMenu = findViewById(R.id.btn_start_shadow_at_menu);
        imgvPuzzleGameTitleAtMainActivity = findViewById(R.id.imgv_puzzle_game_title_at_main_activity);

        btnStartAtMenu.setOnClickListener(view -> {
            Intent toLevelSelectActivityIntent = new Intent(MainActivity.this, LevelSelectActivity.class);
            startActivity(toLevelSelectActivityIntent);
        });

        // set on-click-listener for animations
        imgvPuzzleGameTitleAtMainActivity.setOnClickListener(view -> {
            if (puzzleGameTitleAnimationStatus == 0) {
                imgvPuzzleGameTitleAtMainActivity.startAnimation(animScaleLittleWithBouncing);
                puzzleGameTitleAnimationStatus = 1;
            } else if (puzzleGameTitleAnimationStatus == 1) {
                imgvPuzzleGameTitleAtMainActivity.startAnimation(animScaleLargeWithOvershooting);
                puzzleGameTitleAnimationStatus = 0;
            }
        });
    }

    private void initAnimations() {
        puzzleGameTitleAnimationStatus = 0;

        animScaleLittleWithBouncing = new ScaleAnimation(1.0F, 0.5F, 1.0F, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        animScaleLittleWithBouncing.setDuration(2000L);
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
}
