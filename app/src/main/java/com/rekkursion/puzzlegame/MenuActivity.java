package com.rekkursion.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {
    private TextView txtvPlayOption;
    private TextView txtvRankingOption;
    private TextView txtvSettingsOption;

    private ImageView imgvBackToMainFromMenu;

    private View.OnTouchListener menuOptionsOnTouchListener = (view, motionEvent) -> {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Animation animOptionPressed = AnimationUtils.loadAnimation(MenuActivity.this, R.anim.scale_animation_view_pressed);
                animOptionPressed.setFillAfter(true);
                view.startAnimation(animOptionPressed);
                return true;

            case MotionEvent.ACTION_UP:
                txtvPlayOption.setEnabled(false);
                txtvRankingOption.setEnabled(false);
                txtvSettingsOption.setEnabled(false);

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
        SoundPoolManager.getInstance().play("se_maoudamashii_click_entering.mp3");
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initViews();
    }

    @Override
    protected void onResume() {
        txtvPlayOption.setEnabled(true);
        txtvRankingOption.setEnabled(true);
        txtvSettingsOption.setEnabled(true);

        super.onResume();
    }

    private void initViews() {
        txtvPlayOption = findViewById(R.id.txtv_play_option_at_menu_activity);
        txtvRankingOption = findViewById(R.id.txtv_ranking_option_at_menu_activity);
        txtvSettingsOption = findViewById(R.id.txtv_settings_option_at_menu_activity);

        txtvPlayOption.setOnTouchListener(menuOptionsOnTouchListener);
        txtvRankingOption.setOnTouchListener(menuOptionsOnTouchListener);
        txtvSettingsOption.setOnTouchListener(menuOptionsOnTouchListener);

        txtvPlayOption.setOnClickListener(menuOptionsOnClickListener);
        txtvRankingOption.setOnClickListener(menuOptionsOnClickListener);
        txtvSettingsOption.setOnClickListener(menuOptionsOnClickListener);

        imgvBackToMainFromMenu = findViewById(R.id.imgv_back_to_main_from_menu);

        imgvBackToMainFromMenu.setOnClickListener(view -> {
            SoundPoolManager.getInstance().play("se_maoudamashii_click_leaving.mp3");
            finish();
        });
    }
}
