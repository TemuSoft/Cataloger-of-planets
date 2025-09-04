package com.Catalogerofplanets.COP2008;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CoreActivity extends AppCompatActivity implements View.OnTouchListener {
    private ImageView back;
    private TextView planets_explored, coin;
    private LinearLayout layout_canvas, layout_time, layout_progress;
    private LinearLayout layout_complete, layout_lose;
    private TextView name, time;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageView create;
    private LinearLayout layout_vertical;
    private boolean isMute, soundMute;
    private String lang;
    private LayoutInflater inflate;
    private Intent intent;
    private int all_coin;
    private CoreView coreView;
    private Handler handler;
    private int lastLevelActive, playLevel;

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

        all_coin = sharedPreferences.getInt("coin", 0);

        setContentView(R.layout.activity_core);
        handler = new Handler();

        back = findViewById(R.id.back);
        planets_explored = findViewById(R.id.planets_explored);
        coin = findViewById(R.id.coin);

        layout_canvas = findViewById(R.id.layout_canvas);
        layout_time = findViewById(R.id.layout_time);
        layout_progress = findViewById(R.id.layout_progress);

        layout_complete = findViewById(R.id.layout_complete);
        layout_lose = findViewById(R.id.layout_lose);

        name = findViewById(R.id.name);
        time = findViewById(R.id.time);

        back.setOnClickListener(View -> {
            finish();
        });

        layout_canvas.post(() -> {
            layout_canvas.removeAllViews();

            int w = layout_canvas.getWidth();
            int h = layout_canvas.getHeight();

            coreView = new CoreView(this, w, h, getResources(), 0);
            coreView.setLayoutParams(new LinearLayout.LayoutParams(w, h));
            layout_canvas.addView(coreView);

            layout_canvas.setOnTouchListener(this);
            reloading_UI();

        });

        planets_explored.setText(getResources().getString(R.string.planets_explored) + " " + (lastLevelActive - 1));
        coin.setText(getResources().getString(R.string.coins) + " " + all_coin);
    }

    private void reloading_UI() {
        Runnable r = new Runnable() {
            public void run() {
                if (coreView.isPlaying) {
                    if (!coreView.game_won && !coreView.game_over) coreView.update();

                    time.setText(Player.convertMillisToTime(System.currentTimeMillis() - coreView.start_time));

                    if (coreView.game_won && coreView.game_won_time + coreView.duration < System.currentTimeMillis())
                        game_won();

                    if (coreView.game_over && coreView.game_over_time + coreView.duration < System.currentTimeMillis())
                        game_over();

                    reloading_UI();
                }
            }
        };
        handler.postDelayed(r, 20);
    }

    private void game_won() {
        coreView.isPlaying = false;
        layout_complete.setVisibility(VISIBLE);
        layout_time.setVisibility(INVISIBLE);
        layout_progress.setVisibility(INVISIBLE);

        ImageView planet = findViewById(R.id.planet_won);
        TextView coin = findViewById(R.id.coin_won);
        Button go_to_planets = findViewById(R.id.go_to_planets_won);
        Button menu = findViewById(R.id.menu_won);

        int group_index = (playLevel - 1) % Player.planet_devider;
        int im = getResources().getIdentifier("cha_" + group_index + "_" + coreView.item_index, "drawable", getPackageName());
        planet.setImageResource(im);
        coin.setText("+" + coreView.score + " " + getResources().getString(R.string.coins));

        editor.putString("core_status", "completed");
        editor.putInt("core_coin", coreView.score);
        editor.apply();

        go_to_planets.setOnClickListener(View -> {
            Player.button(soundMute);
            intent = new Intent(CoreActivity.this, SpaceActivity.class);
            startActivity(intent);
            finish();
        });

        menu.setOnClickListener(View -> {
            Player.button(soundMute);
            intent = new Intent(CoreActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void game_over() {
        coreView.isPlaying = false;
        layout_lose.setVisibility(VISIBLE);
        layout_time.setVisibility(INVISIBLE);
        layout_progress.setVisibility(INVISIBLE);

        ImageView planet = findViewById(R.id.planet_lose);
        TextView coin = findViewById(R.id.coin_lose);
        Button go_to_planets = findViewById(R.id.go_to_planets_lose);
        Button menu = findViewById(R.id.menu_lose);

        int group_index = (playLevel - 1) % Player.planet_devider;
        int im = getResources().getIdentifier("cha_" + group_index + "_" + coreView.item_index, "drawable", getPackageName());
        planet.setImageResource(im);
        coin.setText("0 " + getResources().getString(R.string.coins));

        editor.putString("core_status", "closed");
        editor.apply();

        go_to_planets.setOnClickListener(View -> {
            Player.button(soundMute);
            intent = new Intent(CoreActivity.this, SpaceActivity.class);
            startActivity(intent);
            finish();
        });

        menu.setOnClickListener(View -> {
            Player.button(soundMute);
            intent = new Intent(CoreActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
//        if (!isMute)
//            Player.all_screens.pause();

        try {
            coreView.pause_time = System.currentTimeMillis();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        isMute = sharedPreferences.getBoolean("isMute", false);
//        if (!isMute)
//            Player.all_screens.start();

        try {
            if (coreView.pause_time != 0) {
                long gap = System.currentTimeMillis() - coreView.pause_time;
                coreView.start_time += gap;
                coreView.pause_time = 0;
            }
        } catch (Exception e) {
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
                if (!coreView.game_won && !coreView.game_over) processActionUp(x, y);
                break;
        }
        return true;
    }

    private void processActionDown(int xp, int yp) {
        Rect clicked = new Rect(xp, yp, xp, yp);

    }

    private void processActionUp(int xp, int yp) {
        Rect clicked = new Rect(xp, yp, xp, yp);

        coreView.add_click(xp, yp);
    }

    private void processActionMove(int xp, int yp) {

    }
}