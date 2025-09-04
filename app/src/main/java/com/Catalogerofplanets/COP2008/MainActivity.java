package com.Catalogerofplanets.COP2008;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button start;
    private TextView planets_explored, coin;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageView create;
    private LinearLayout layout_vertical;
    private boolean isMute, soundMute;
    private String lang;
    private LayoutInflater inflate;
    private Intent intent;
    private int all_coin;
    private int lastLevelActive, playLevel;
    private int total_explored;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("logerofplanetsCO", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isMute = sharedPreferences.getBoolean("isMute", false);
        soundMute = sharedPreferences.getBoolean("soundMute", false);
        lastLevelActive = sharedPreferences.getInt("lastLevelActive", 1);
        playLevel = sharedPreferences.getInt("playLevel", 1);
        total_explored = sharedPreferences.getInt("total_explored", 0);

        all_coin = sharedPreferences.getInt("coin", 0);

        setContentView(R.layout.activity_main);

        start = findViewById(R.id.start);
        planets_explored = findViewById(R.id.planets_explored);
        coin = findViewById(R.id.coin);


        start.setOnClickListener(View -> {
            intent = new Intent(MainActivity.this, SpaceActivity.class);
            startActivity(intent);
            finish();
        });

        planets_explored.setText(getResources().getString(R.string.planets_explored) + " " + total_explored);
        coin.setText(getResources().getString(R.string.coins) + " " + all_coin);
    }


    @Override
    protected void onPause() {
        super.onPause();
//        if (!isMute)
//            Player.all_screens.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        isMute = sharedPreferences.getBoolean("isMute", false);
//        if (!isMute)
//            Player.all_screens.start();
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}