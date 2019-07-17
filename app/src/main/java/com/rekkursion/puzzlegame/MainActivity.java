package com.rekkursion.puzzlegame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btnStartAtMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartAtMenu = findViewById(R.id.btn_start_at_menu);
        btnStartAtMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toLevelSelectActivityIntent = new Intent(MainActivity.this, LevelSelectActivity.class);
                startActivity(toLevelSelectActivityIntent);
            }
        });
    }
}
