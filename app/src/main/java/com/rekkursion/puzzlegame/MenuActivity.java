package com.rekkursion.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MenuActivity extends AppCompatActivity {
    private LinearLayout llyOptionsAtMenuActivityContainer;

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
                int childrenCount = llyOptionsAtMenuActivityContainer.getChildCount();
                for (int k = 0; k < childrenCount; ++k)
                    llyOptionsAtMenuActivityContainer.getChildAt(k).setEnabled(false);

                Animation animOptionUnpressed = AnimationUtils.loadAnimation(MenuActivity.this, R.anim.scale_animation_view_unpressed);
                animOptionUnpressed.setFillAfter(true);
                animOptionUnpressed.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view.performClick();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                view.startAnimation(animOptionUnpressed);
                return true;

            default: return true;
        }
    };

    private View.OnClickListener menuOptionsOnClickListener = view -> {
        switch (view.getId()) {
            case R.id.txtv_play_option_at_menu_activity:
                Intent toLevelSelectActivityIntent = new Intent(MenuActivity.this, LevelSelectActivity.class);
                startActivity(toLevelSelectActivityIntent);
                break;

            case R.id.txtv_ranking_option_at_menu_activity:
                // set new ranking record at ranking-activity
                if (RankingActivity.newRankingRecord != null)
                    RankingActivity.newRankingRecord = null;

                // create intent and go to ranking-activity
                Intent intentToRankingActivity = new Intent(MenuActivity.this, RankingActivity.class);
                startActivity(intentToRankingActivity);
                break;

            case R.id.txtv_settings_option_at_menu_activity:
                break;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initViews();
    }

    private void initViews() {
        llyOptionsAtMenuActivityContainer = findViewById(R.id.lly_options_at_menu_activity_container);

        txtvPlayOption = findViewById(R.id.txtv_play_option_at_menu_activity);
        txtvRankingOption = findViewById(R.id.txtv_ranking_option_at_menu_activity);
        txtvSettingsOption = findViewById(R.id.txtv_settings_option_at_menu_activity);

        txtvPlayOption.setEnabled(true);
        txtvRankingOption.setEnabled(true);
        txtvSettingsOption.setEnabled(true);

        txtvPlayOption.setOnTouchListener(menuOptionsOnTouchListener);
        txtvRankingOption.setOnTouchListener(menuOptionsOnTouchListener);
        txtvSettingsOption.setOnTouchListener(menuOptionsOnTouchListener);

        txtvPlayOption.setOnClickListener(menuOptionsOnClickListener);
        txtvRankingOption.setOnClickListener(menuOptionsOnClickListener);
        txtvSettingsOption.setOnClickListener(menuOptionsOnClickListener);
    }
}
