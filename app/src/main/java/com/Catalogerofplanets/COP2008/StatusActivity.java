package com.Catalogerofplanets.COP2008;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class StatusActivity extends AppCompatActivity {
    private ImageView back, planet;
    private TextView name, coin, planets_explored;
    private LinearLayout layout_game_done;
    private Button next, shop;

    private TextView text_one, one_play_again, award_one, status_one;
    private TextView text_two, two_play_again, award_two, status_two;
    private TextView text_three, three_play_again, award_three, status_three;
    private LinearLayout layout_one, layout_two, layout_three;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageView create;
    private LinearLayout layout_vertical;
    private boolean isMute, soundMute;
    private String lang;
    private LayoutInflater inflate;
    private Intent intent;
    private int all_planets_explored, all_coin;
    private int group_index, item_index;
    private int atmosphere_coin, ground_coin, core_coin;
    private String atmosphere_status, ground_status, core_status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("logerofplanetsCO", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isMute = sharedPreferences.getBoolean("isMute", false);
        soundMute = sharedPreferences.getBoolean("soundMute", false);
        lang = sharedPreferences.getString("lang", "");
        all_planets_explored = sharedPreferences.getInt("item_index", 0);
        all_coin = sharedPreferences.getInt("coin", 0);
        group_index = sharedPreferences.getInt("group_index", 0);
        item_index = sharedPreferences.getInt("item_index", 0);

        setContentView(R.layout.activity_status);

        back = findViewById(R.id.back);
        layout_game_done = findViewById(R.id.layout_game_done);
        shop = findViewById(R.id.shop);
        next = findViewById(R.id.next);

        layout_one = findViewById(R.id.layout_one);
        text_one = findViewById(R.id.text_one);
        one_play_again = findViewById(R.id.one_play_again);
        award_one = findViewById(R.id.award_one);
        status_one = findViewById(R.id.status_one);

        layout_two = findViewById(R.id.layout_two);
        text_two = findViewById(R.id.text_two);
        two_play_again = findViewById(R.id.two_play_again);
        award_two = findViewById(R.id.award_two);
        status_two = findViewById(R.id.status_two);

        layout_three = findViewById(R.id.layout_three);
        text_three = findViewById(R.id.text_three);
        three_play_again = findViewById(R.id.three_play_again);
        award_three = findViewById(R.id.award_three);
        status_three = findViewById(R.id.status_three);

        planet = findViewById(R.id.planet);
        name = findViewById(R.id.name);
        planets_explored = findViewById(R.id.planets_explored);
        coin = findViewById(R.id.coin);

        back.setOnClickListener(View -> {
            intent = new Intent(StatusActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        planets_explored.setText(getResources().getString(R.string.planets_explored) + " " + all_planets_explored);
        coin.setText(getResources().getString(R.string.coins) + " " + all_coin);

        int p = getResources().getIdentifier("cha_" + group_index + "_" + item_index, "drawable", getPackageName());
        planet.setImageResource(p);

        get_value();
    }

    private void get_value() {
        atmosphere_coin = sharedPreferences.getInt("atmosphere_coin", 0);
        ground_coin = sharedPreferences.getInt("ground_coin", 0);
        core_coin = sharedPreferences.getInt("core_coin", 0);

        atmosphere_status = sharedPreferences.getString("atmosphere_status", "ready");
        ground_status = sharedPreferences.getString("ground_status", "ready");
        core_status = sharedPreferences.getString("core_status", "ready");


        set_UI();
    }

    private void set_UI() {
        award_one.setText(getResources().getString(R.string.award) + "" + atmosphere_coin);
        award_two.setText(getResources().getString(R.string.award) + "" + ground_coin);
        award_three.setText(getResources().getString(R.string.award) + "" + core_coin);

        coin.setText(getResources().getString(R.string.coins) + " " + (all_coin + atmosphere_coin + ground_coin + core_coin));

        if (atmosphere_status.equals("ready")) {
            layout_one.setBackgroundResource(R.drawable.green_rect);
            status_one.setText(getResources().getString(R.string.ready));
            one_play_again.setBackgroundResource(R.drawable.green_rect);
            text_one.setTextColor(getResources().getColor(R.color.green));
        } else if (atmosphere_status.equals("completed")) {
            layout_one.setBackgroundResource(R.drawable.gray_rect);
            status_one.setText(getResources().getString(R.string.completed));
            one_play_again.setBackgroundResource(R.drawable.gray_rect);
            text_one.setTextColor(getResources().getColor(R.color.white));
        } else {
            layout_one.setBackgroundResource(R.drawable.dark_rect);
            status_one.setText(getResources().getString(R.string.closed));
            one_play_again.setBackgroundResource(R.drawable.dark_rect);
            text_one.setTextColor(getResources().getColor(R.color.gray));
        }


        if (ground_status.equals("ready")) {
            layout_two.setBackgroundResource(R.drawable.green_rect);
            status_two.setText(getResources().getString(R.string.ready));
            two_play_again.setBackgroundResource(R.drawable.green_rect);
            text_two.setTextColor(getResources().getColor(R.color.green));
        } else if (ground_status.equals("completed")) {
            layout_two.setBackgroundResource(R.drawable.gray_rect);
            status_two.setText(getResources().getString(R.string.completed));
            two_play_again.setBackgroundResource(R.drawable.gray_rect);
            text_two.setTextColor(getResources().getColor(R.color.white));
        } else {
            layout_two.setBackgroundResource(R.drawable.dark_rect);
            status_two.setText(getResources().getString(R.string.closed));
            two_play_again.setBackgroundResource(R.drawable.dark_rect);
            text_two.setTextColor(getResources().getColor(R.color.gray));
        }


        if (core_status.equals("ready")) {
            layout_three.setBackgroundResource(R.drawable.green_rect);
            status_three.setText(getResources().getString(R.string.ready));
            three_play_again.setBackgroundResource(R.drawable.green_rect);
            text_three.setTextColor(getResources().getColor(R.color.green));
        } else if (core_status.equals("completed")) {
            layout_three.setBackgroundResource(R.drawable.gray_rect);
            status_three.setText(getResources().getString(R.string.completed));
            three_play_again.setBackgroundResource(R.drawable.gray_rect);
            text_three.setTextColor(getResources().getColor(R.color.white));
        } else {
            layout_three.setBackgroundResource(R.drawable.dark_rect);
            status_three.setText(getResources().getString(R.string.closed));
            three_play_again.setBackgroundResource(R.drawable.dark_rect);
            text_three.setTextColor(getResources().getColor(R.color.gray));
        }

        if (atmosphere_status.equals("completed") && ground_status.equals("completed") && core_status.equals("completed")) {
            layout_game_done.setVisibility(VISIBLE);

            shop.setOnClickListener(View -> {
                editor.putInt("coin", all_coin + atmosphere_coin + ground_coin + core_coin);
                editor.apply();

                intent = new Intent(StatusActivity.this, StoreActivity.class);
                startActivity(intent);
                finish();
            });

            next.setOnClickListener(View -> {
                editor.putInt("coin", all_coin + atmosphere_coin + ground_coin + core_coin);
                editor.apply();

                // purchaed amount will deccided thi button activation
                Toast.makeText(this, "Next processing will be added her is it's required!", Toast.LENGTH_SHORT).show();
            });
        }
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
        get_value();

//        isMute = sharedPreferences.getBoolean("isMute", false);
//        if (!isMute)
//            Player.all_screens.start();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}