package com.Catalogerofplanets.COP2008;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SpaceActivity extends AppCompatActivity {
    private ImageView back, backward, forward;
    private TextView chapter, coin, planets_explored;
    private Button shop;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageView create;
    private LinearLayout layout_vertical;
    private boolean isMute, soundMute;
    private String lang;
    private LayoutInflater inflate;
    private Intent intent;
    private int lastLevelActive, playLevel;
    private int all_coin, award_coin;
    private int[][] images = new int[][]{{R.drawable.cha_0_0, R.drawable.cha_0_1, R.drawable.cha_0_2, R.drawable.cha_0_3}, {R.drawable.cha_1_0, R.drawable.cha_1_1, R.drawable.cha_1_2, R.drawable.cha_1_3}, {R.drawable.cha_2_0, R.drawable.cha_2_1, R.drawable.cha_2_2, R.drawable.cha_2_3}, {R.drawable.cha_3_0, R.drawable.cha_3_1, R.drawable.cha_3_2, R.drawable.cha_3_3},};

    private int item_one_purchased, item_two_purchased, item_three_purchased;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("logerofplanetsCO", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isMute = sharedPreferences.getBoolean("isMute", false);
        soundMute = sharedPreferences.getBoolean("soundMute", false);
        lang = sharedPreferences.getString("lang", "");
        all_coin = sharedPreferences.getInt("coin", 0);
        lastLevelActive = sharedPreferences.getInt("lastLevelActive", 1);
        playLevel = sharedPreferences.getInt("playLevel", 1);
        item_one_purchased = sharedPreferences.getInt("item_one_purchased", 0);
        item_two_purchased = sharedPreferences.getInt("item_two_purchased", 0);
        item_three_purchased = sharedPreferences.getInt("item_three_purchased", 0);

        award_coin = (playLevel * 10 + 100) * 3;

        setContentView(R.layout.activity_space);

        back = findViewById(R.id.back);
        backward = findViewById(R.id.backward);
        forward = findViewById(R.id.forward);

        chapter = findViewById(R.id.chapter);
        planets_explored = findViewById(R.id.planets_explored);
        coin = findViewById(R.id.coin);

        shop = findViewById(R.id.shop);

        back.setOnClickListener(View -> {
            intent = new Intent(SpaceActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        backward.setOnClickListener(View -> {
            playLevel--;
            process_UI();
        });

        forward.setOnClickListener(View -> {
            playLevel++;
            process_UI();
        });

        shop.setOnClickListener(View -> {
            Player.button(soundMute);

            intent = new Intent(SpaceActivity.this, StoreActivity.class);
            startActivity(intent);
            finish();

        });

        editor.putInt("atmosphere_coin", 0);
        editor.putInt("ground_coin", 0);
        editor.putInt("core_coin", 0);
        editor.putString("atmosphere_status", "ready");
        editor.putString("ground_status", "ready");
        editor.putString("core_status", "ready");
        editor.apply();

        process_UI();
    }

    private void process_UI() {
        backward.setVisibility(VISIBLE);
        forward.setVisibility(VISIBLE);
        if (playLevel <= 1) {
            playLevel = 1;
            backward.setVisibility(INVISIBLE);
        }
        if (playLevel >= lastLevelActive) {
            playLevel = lastLevelActive;
            forward.setVisibility(INVISIBLE);
        }

        chapter.setText(getResources().getString(R.string.chapter) + " " + playLevel);

        load_spaces();
        planets_explored.setText(getResources().getString(R.string.planets_explored) + " " + (lastLevelActive - 1));
        coin.setText(getResources().getString(R.string.coins) + " " + all_coin);
    }

    private void load_spaces() {
        ImageView planet1 = findViewById(R.id.planet1);
        ImageView planet2 = findViewById(R.id.planet2);
        ImageView planet3 = findViewById(R.id.planet3);
        ImageView planet4 = findViewById(R.id.planet4);

        int active_space = (playLevel - 1) % Player.planet_devider;
        planet1.setImageResource(images[active_space][0]);
        planet2.setImageResource(images[active_space][1]);
        planet3.setImageResource(images[active_space][2]);
        planet4.setImageResource(images[active_space][3]);

        ImageView info1 = findViewById(R.id.info1);
        ImageView info2 = findViewById(R.id.info2);
        ImageView info3 = findViewById(R.id.info3);
        ImageView info4 = findViewById(R.id.info4);

        info1.setOnClickListener(v -> showPopup(v, "Easy", 1));
        info2.setOnClickListener(v -> showPopup(v, "Normal", 2));
        info3.setOnClickListener(v -> showPopup(v, "Difficult", 3));
        info4.setOnClickListener(v -> showPopup(v, "Test", 4));
    }

    private void showPopup(View anchorView, String type, int index) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_info, null);
        TextView planet = popupView.findViewById(R.id.planet);
        TextView mode = popupView.findViewById(R.id.mode);
        TextView award = popupView.findViewById(R.id.award);
        TextView start = popupView.findViewById(R.id.start);
        TextView alert = popupView.findViewById(R.id.alert);


        planet.setText(getResources().getString(R.string.planet) + " " + index);
        mode.setText(type);
        award.setText(getResources().getString(R.string.award) + " " + award_coin);

        boolean is_valid = item_one_purchased + item_two_purchased + item_three_purchased >= index;
        is_valid = index == 4 || is_valid;
        if (is_valid) {
            start.setTextColor(getResources().getColor(R.color.white));
            start.setBackgroundResource(R.drawable.blue_rect);
            start.setEnabled(true);
            alert.setVisibility(GONE);
        } else {
            start.setTextColor(getResources().getColor(R.color.gray));
            start.setBackgroundResource(R.drawable.dark_rect);
            start.setEnabled(false);
            alert.setVisibility(VISIBLE);
        }

        start.setOnClickListener(View -> {
            editor.putInt("playLevel", playLevel);
            editor.putInt("item_index", index - 1);
            editor.apply();

            intent = new Intent(SpaceActivity.this, StatusActivity.class);
            startActivity(intent);
        });
        alert.setVisibility(VISIBLE);

        PopupWindow popupWindow = new PopupWindow(popupView, 550, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        popupWindow.showAsDropDown(anchorView, 0, 10);
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
        finish();
    }

}