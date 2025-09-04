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

public class GroundActivity extends AppCompatActivity implements View.OnTouchListener {
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
    private GroundView groundView;
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

        setContentView(R.layout.activity_ground);
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

            groundView = new GroundView(this, w, h, getResources(), 0);
            groundView.setLayoutParams(new LinearLayout.LayoutParams(w, h));
            layout_canvas.addView(groundView);

            layout_canvas.setOnTouchListener(this);
            reloading_UI();
        });

        planets_explored.setText(getResources().getString(R.string.planets_explored) + " " + (lastLevelActive - 1));
        coin.setText(getResources().getString(R.string.coins) + " " + all_coin);
    }

    private void reloading_UI() {
        Runnable r = new Runnable() {
            public void run() {
                if (groundView.isPlaying) {
                    if (!groundView.game_won && !groundView.game_over) groundView.update();

                    time.setText(Player.convertMillisToTime(System.currentTimeMillis() - groundView.start_time));

                    if (groundView.game_won && groundView.game_won_time + groundView.duration < System.currentTimeMillis())
                        game_won();

                    if (groundView.game_over && groundView.game_over_time + groundView.duration < System.currentTimeMillis())
                        game_over();

                    reloading_UI();
                }
            }
        };
        handler.postDelayed(r, 20);
    }

    private void game_won() {
        groundView.isPlaying = false;
        layout_complete.setVisibility(VISIBLE);
        layout_time.setVisibility(INVISIBLE);
        layout_progress.setVisibility(INVISIBLE);

        ImageView planet = findViewById(R.id.planet_won);
        TextView coin = findViewById(R.id.coin_won);
        Button go_to_planets = findViewById(R.id.go_to_planets_won);
        Button menu = findViewById(R.id.menu_won);

        int group_index = (playLevel - 1) % Player.planet_devider;
        int im = getResources().getIdentifier("cha_" + group_index + "_" + groundView.item_index, "drawable", getPackageName());
        planet.setImageResource(im);
        coin.setText("+" + groundView.score + " " + getResources().getString(R.string.coins));

        editor.putString("ground_status", "completed");
        groundView.score = playLevel * 10 + 100;
        editor.putInt("ground_coin", groundView.score);
        editor.apply();

        go_to_planets.setOnClickListener(View -> {
            Player.button(soundMute);
            intent = new Intent(GroundActivity.this, SpaceActivity.class);
            startActivity(intent);
            finish();
        });

        menu.setOnClickListener(View -> {
            Player.button(soundMute);
            intent = new Intent(GroundActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void game_over() {
        groundView.isPlaying = false;
        layout_lose.setVisibility(VISIBLE);
        layout_time.setVisibility(INVISIBLE);
        layout_progress.setVisibility(INVISIBLE);

        ImageView planet = findViewById(R.id.planet_lose);
        TextView coin = findViewById(R.id.coin_lose);
        Button go_to_planets = findViewById(R.id.go_to_planets_lose);
        Button menu = findViewById(R.id.menu_lose);

        int group_index = (playLevel - 1) % Player.planet_devider;
        int im = getResources().getIdentifier("cha_" + group_index + "_" + groundView.item_index, "drawable", getPackageName());
        planet.setImageResource(im);
        coin.setText("0 " + getResources().getString(R.string.coins));

        editor.putString("ground_status", "closed");
        editor.apply();

        go_to_planets.setOnClickListener(View -> {
            Player.button(soundMute);
            intent = new Intent(GroundActivity.this, SpaceActivity.class);
            startActivity(intent);
            finish();
        });

        menu.setOnClickListener(View -> {
            Player.button(soundMute);
            intent = new Intent(GroundActivity.this, MainActivity.class);
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
            groundView.pause_time = System.currentTimeMillis();
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
            if (groundView.pause_time != 0) {
                long gap = System.currentTimeMillis() - groundView.pause_time;
                groundView.start_time += gap;
                groundView.pause_time = 0;
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
                if (!groundView.game_won && !groundView.game_over && !groundView.show_time_over) processActionDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!groundView.game_won && !groundView.game_over && groundView.tap_index != -1)
                    processActionMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                if (!groundView.game_won && !groundView.game_over) processActionUp(x, y);
                break;
        }
        return true;
    }

    private void processActionDown(int xp, int yp) {
        Rect clicked = new Rect(xp, yp, xp, yp);

        for (int i = 0; i < groundView.rect_data.size(); i++) {
            int rx = groundView.rect_data.get(i).get(0);
            int ry = groundView.rect_data.get(i).get(1);
            int x = groundView.rect_data.get(i).get(2);
            int y = groundView.rect_data.get(i).get(3);
            int index = groundView.rect_data.get(i).get(4);
            int ux = groundView.rect_data.get(i).get(5);
            int uy = groundView.rect_data.get(i).get(6);
            int uindex = groundView.rect_data.get(i).get(7);

            if (uindex == -1) continue;

            Rect soil = new Rect(x, y, x + groundView.s_w, y + groundView.s_h);
            if (Rect.intersects(soil, clicked)) {
                groundView.move_is_internal = true;
                groundView.tap_index = i;
                groundView.tap_x = xp;
                groundView.tap_y = yp;
                break;
            }
        }

        for (int i = 0; i < groundView.eclipse_data.size(); i++) {
            int rx = groundView.eclipse_data.get(i).get(0);
            int ry = groundView.eclipse_data.get(i).get(1);
            int x = groundView.eclipse_data.get(i).get(2);
            int y = groundView.eclipse_data.get(i).get(3);
            int index = groundView.eclipse_data.get(i).get(4);
            int ux = groundView.eclipse_data.get(i).get(5);
            int uy = groundView.eclipse_data.get(i).get(6);
            int uindex = groundView.eclipse_data.get(i).get(7);

            if (uindex == -1) continue;

            Rect soil = new Rect(x, y, x + groundView.s_w, y + groundView.s_h);
            if (Rect.intersects(soil, clicked)) {
                groundView.move_is_internal = false;
                groundView.tap_index = i;
                groundView.tap_x = xp;
                groundView.tap_y = yp;
                break;
            }
        }

    }

    private void processActionUp(int xp, int yp) {
        Rect clicked = new Rect(xp, yp, xp, yp);


        if (groundView.tap_index != -1) {
            int xx = groundView.eclipse_data.get(groundView.tap_index).get(5);
            int yy = groundView.eclipse_data.get(groundView.tap_index).get(6);
            if (groundView.move_is_internal) {
                xx = groundView.rect_data.get(groundView.tap_index).get(5);
                yy = groundView.rect_data.get(groundView.tap_index).get(6);
            }
            int m = groundView.s_h / 6;
            Rect move = new Rect(xx + m, yy + m, xx + groundView.s_w - m, yy + groundView.s_h - m);

            for (int i = 0; i < groundView.rect_data.size(); i++) {
                if (groundView.tap_index == i && groundView.move_is_internal)
                    continue;

                int rx = groundView.rect_data.get(i).get(0);
                int ry = groundView.rect_data.get(i).get(1);
                int x = groundView.rect_data.get(i).get(2);
                int y = groundView.rect_data.get(i).get(3);
                int index = groundView.rect_data.get(i).get(4);
                int ux = groundView.rect_data.get(i).get(5);
                int uy = groundView.rect_data.get(i).get(6);
                int uindex = groundView.rect_data.get(i).get(7);

                if (uindex != -1) continue;

                Rect soil = new Rect(x + m, y + m, x + groundView.s_w - m, y + groundView.s_h - m);

                if (Rect.intersects(soil, move)) {
                    groundView.rect_data.get(i).set(7, groundView.rect_data.get(groundView.tap_index).get(7));
                    groundView.rect_data.get(groundView.tap_index).set(7, -1);
                    groundView.check_game_status();
                    break;
                }
            }

            groundView.rect_data.get(groundView.tap_index).set(5, groundView.rect_data.get(groundView.tap_index).get(2));
            groundView.rect_data.get(groundView.tap_index).set(6, groundView.rect_data.get(groundView.tap_index).get(3));
        }

        groundView.move_is_internal = false;
        groundView.tap_index = -1;
        groundView.tap_x = -1;
        groundView.tap_y = -1;
    }

    private void processActionMove(int xp, int yp) {
        int x_dif = xp - groundView.tap_x;
        int y_dif = yp - groundView.tap_y;

        if (groundView.move_is_internal) {
            int x = groundView.rect_data.get(groundView.tap_index).get(5);
            int y = groundView.rect_data.get(groundView.tap_index).get(6);

            groundView.rect_data.get(groundView.tap_index).set(5, x + x_dif);
            groundView.rect_data.get(groundView.tap_index).set(6, y + y_dif);
        } else {
            int x = groundView.eclipse_data.get(groundView.tap_index).get(5);
            int y = groundView.eclipse_data.get(groundView.tap_index).get(6);

            groundView.eclipse_data.get(groundView.tap_index).set(5, x + x_dif);
            groundView.eclipse_data.get(groundView.tap_index).set(6, y + y_dif);
        }
    }
}