package com.rekkursion.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MenuActivity extends AppCompatActivity {
    private TextView txtvPlayOption;
    private TextView txtvRankingOption;
    private TextView txtvSettingsOption;

    private View.OnTouchListener menuOptionsOnTouchListener = (view, motionEvent) -> {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Animation animOptionPressed = AnimationUtils.loadAnimation(MenuActivity.this, R.anim.scale_animation_view_pressed);
                animOptionPressed.setFillAfter(true);
                view.startAnimation(animOptionPressed);
                return true;

            case MotionEvent.ACTION_UP:
                Animation animOptionUnpressed = AnimationUtils.loadAnimation(MenuActivity.this, R.anim.scale_animation_view_unpressed);
                animOptionUnpressed.setFillAfter(true);
                view.startAnimation(animOptionUnpressed);
                return true;

            default: return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initViews();
    }

    private void initViews() {
        txtvPlayOption = findViewById(R.id.txtv_play_option_at_menu_activity);
        txtvRankingOption = findViewById(R.id.txtv_ranking_option_at_menu_activity);
        txtvSettingsOption = findViewById(R.id.txtv_settings_option_at_menu_activity);

        txtvPlayOption.setOnTouchListener(menuOptionsOnTouchListener);
        txtvRankingOption.setOnTouchListener(menuOptionsOnTouchListener);
        txtvSettingsOption.setOnTouchListener(menuOptionsOnTouchListener);
    }
}
