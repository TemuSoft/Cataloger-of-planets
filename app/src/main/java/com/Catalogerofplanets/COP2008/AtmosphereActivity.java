package com.Catalogerofplanets.COP2008;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AtmosphereActivity extends AppCompatActivity implements View.OnTouchListener {
    private ImageView back;
    private TextView planets_explored, coin;
    private LinearLayout layout_canvas, layout_time, layout_progress;
    private LinearLayout layout_complete, layout_lose;
    private ProgressBar progressBar_red, progressBar_blue, progressBar_green;
    private TextView name, time;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageView create;
    private LinearLayout layout_vertical;
    private boolean isMute, soundMute;
    private String lang;
    private LayoutInflater inflate;
    private Intent intent;
    private int all_planets_explored, all_coin;
    private AtmosphereView atmosphereView;
    private Handler handler;
    private long start_time = System.currentTimeMillis();
    private long pause_time;
    private int group_index, item_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("logerofplanetsCO", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isMute = sharedPreferences.getBoolean("isMute", false);
        soundMute = sharedPreferences.getBoolean("soundMute", false);
        group_index = sharedPreferences.getInt("group_index", 0);
        item_index = sharedPreferences.getInt("item_index", 0);

        all_planets_explored = sharedPreferences.getInt("planets_explored", 0);
        all_coin = sharedPreferences.getInt("coin", 0);

        setContentView(R.layout.activity_atmosphere);
        handler = new Handler();

        back = findViewById(R.id.back);
        planets_explored = findViewById(R.id.planets_explored);
        coin = findViewById(R.id.coin);

        layout_canvas = findViewById(R.id.layout_canvas);
        layout_time = findViewById(R.id.layout_time);
        layout_progress = findViewById(R.id.layout_progress);

        layout_complete = findViewById(R.id.layout_complete);
        layout_lose = findViewById(R.id.layout_lose);

        progressBar_red = findViewById(R.id.progressBar_red);
        progressBar_blue = findViewById(R.id.progressBar_blue);
        progressBar_green = findViewById(R.id.progressBar_green);

        name = findViewById(R.id.name);
        time = findViewById(R.id.time);

        back.setOnClickListener(View -> {
            finish();
        });


        layout_canvas.removeAllViews();
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        int w = point.x;
        int h = point.y;
        atmosphereView = new AtmosphereView(this, w, h, getResources(), 0);
        atmosphereView.setLayoutParams(new LinearLayout.LayoutParams(w, h));
        layout_canvas.addView(atmosphereView);

        layout_canvas.setOnTouchListener(this);
        reloading_UI();
    }

    private void reloading_UI() {
        Runnable r = new Runnable() {
            public void run() {
                if (atmosphereView.isPlaying) {
                    if (!atmosphereView.game_won && !atmosphereView.game_over)
                        atmosphereView.update();

                    time.setText(Player.convertMillisToTime(System.currentTimeMillis() - start_time));

                    if (atmosphereView.game_won && atmosphereView.game_won_time + atmosphereView.duration < System.currentTimeMillis())
                        game_won();

                    if (atmosphereView.game_over && atmosphereView.game_over_time + atmosphereView.duration < System.currentTimeMillis())
                        game_over();

                    update_progress_data();

                    reloading_UI();
                }
            }
        };
        handler.postDelayed(r, 20);
    }

    private void update_progress_data() {
        int progressBar_red_value = 0;
        int progressBar_blue_value = 0;
        int progressBar_green_value = 0;

        progressBar_red.setProgress(progressBar_red_value);
        progressBar_blue.setProgress(progressBar_blue_value);
        progressBar_green.setProgress(progressBar_green_value);

        if (progressBar_red_value >= 100 && progressBar_blue_value >= 100 && progressBar_green_value >= 100) {
            atmosphereView.game_won = true;
            atmosphereView.game_won_time = System.currentTimeMillis();
        }
    }

    private void game_won() {
        atmosphereView.isPlaying = false;
        layout_complete.setVisibility(VISIBLE);
        layout_time.setVisibility(INVISIBLE);
        layout_progress.setVisibility(INVISIBLE);

        ImageView planet = findViewById(R.id.planet_won);
        TextView coin = findViewById(R.id.coin_won);
        Button go_to_planets = findViewById(R.id.go_to_planets_won);
        Button menu = findViewById(R.id.menu_won);

        int im = getResources().getIdentifier("cha_" + group_index + "_" + item_index, "drawable", getPackageName());
        planet.setImageResource(im);
        coin.setText("+" + atmosphereView.score + " " + getResources().getString(R.string.coins));

        go_to_planets.setOnClickListener(View -> {
            Player.button(soundMute);
            intent = new Intent(AtmosphereActivity.this, SpaceActivity.class);
            startActivity(intent);
            finish();
        });

        menu.setOnClickListener(View -> {
            Player.button(soundMute);
            intent = new Intent(AtmosphereActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void game_over() {
        atmosphereView.isPlaying = false;
        layout_lose.setVisibility(VISIBLE);
        layout_time.setVisibility(INVISIBLE);
        layout_progress.setVisibility(INVISIBLE);

        ImageView planet = findViewById(R.id.planet_lose);
        TextView coin = findViewById(R.id.coin_lose);
        Button go_to_planets = findViewById(R.id.go_to_planets_lose);
        Button menu = findViewById(R.id.menu_lose);

        int im = getResources().getIdentifier("cha_" + group_index + "_" + item_index, "drawable", getPackageName());
        planet.setImageResource(im);
        coin.setText(atmosphereView.score + " " + getResources().getString(R.string.coins));

        go_to_planets.setOnClickListener(View -> {
            Player.button(soundMute);
            intent = new Intent(AtmosphereActivity.this, SpaceActivity.class);
            startActivity(intent);
            finish();
        });

        menu.setOnClickListener(View -> {
            Player.button(soundMute);
            intent = new Intent(AtmosphereActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
//        if (!isMute)
//            Player.all_screens.pause();

        pause_time = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        isMute = sharedPreferences.getBoolean("isMute", false);
//        if (!isMute)
//            Player.all_screens.start();

        if (pause_time != 0) {
            long gap = System.currentTimeMillis() - pause_time;
            start_time += gap;
            pause_time = 0;
        }
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                processActionDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                processActionMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                if (!atmosphereView.game_won && !atmosphereView.game_over)
                    processActionUp(x, y);
                break;
        }
        return true;
    }

    private void processActionDown(int x, int y) {

    }

    private void processActionUp(int xp, int yp) {
        Rect clicked = new Rect(xp, yp, xp, yp);

    }

    private void processActionMove(int x, int y) {

    }
}