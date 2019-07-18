package com.rekkursion.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btnStartAtMenu;
    private Button btnStartShadowAtMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartAtMenu = findViewById(R.id.btn_start_at_menu);
        btnStartShadowAtMenu = findViewById(R.id.btn_start_shadow_at_menu);

        btnStartAtMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toLevelSelectActivityIntent = new Intent(MainActivity.this, LevelSelectActivity.class);
                startActivity(toLevelSelectActivityIntent);
            }
        });

        Animation scaleLargeAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_large_and_turn_init_immediately);
        Animation fadeOutAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out_and_turn_init_immediately);

        AnimationSet animSet = new AnimationSet(true);
        animSet.addAnimation(scaleLargeAnim);
        animSet.addAnimation(fadeOutAnim);

        btnStartShadowAtMenu.startAnimation(animSet);
    }
}
