package com.Catalogerofplanets.COP2008;

import static android.view.View.GONE;

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

import org.w3c.dom.Text;

public class StoreActivity extends AppCompatActivity {
    private ImageView back;
    private TextView planets_explored, coin;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private ImageView create;
    private LinearLayout layout_vertical;
    private boolean isMute, soundMute;
    private String lang;
    private LayoutInflater inflate;
    private Intent intent;
    private int all_planets_explored, all_coin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences("logerofplanetsCO", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isMute = sharedPreferences.getBoolean("isMute", false);
        soundMute = sharedPreferences.getBoolean("soundMute", false);

        all_planets_explored = sharedPreferences.getInt("planets_explored", 0);
        all_coin = sharedPreferences.getInt("coin", 0);

        setContentView(R.layout.activity_store);

        inflate = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        layout_vertical = findViewById(R.id.layout_vertical);
        back = findViewById(R.id.back);
        planets_explored = findViewById(R.id.planets_explored);
        coin = findViewById(R.id.coin);


        back.setOnClickListener(View -> {
            intent = new Intent(StoreActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });


        load_store();
    }

    private void load_store() {
        planets_explored.setText(getResources().getString(R.string.planets_explored) + " " + all_planets_explored);
        coin.setText(getResources().getString(R.string.coins) + " " + all_coin);

        String[] o_names = new String[]{"Neuronus", "Neuronus", "Neuronus"};
        String[] names = new String[]{"Enviro-suit", "Driller", "Thermoshield"};
        int[] images = new int[]{R.drawable.store_0, R.drawable.store_1, R.drawable.store_2};
        int[] r_c = new int[]{200, 300, 400};

        boolean[] status = new boolean[]{
                sharedPreferences.getBoolean("store_0", false),
                sharedPreferences.getBoolean("store_1", false),
                sharedPreferences.getBoolean("store_2", false),
        };

        layout_vertical.removeAllViews();


        for (int i = 0; i < 2; i++) {
            View one_store = inflate.inflate(R.layout.one_store, null);

            TextView name = one_store.findViewById(R.id.name);
            ImageView store = one_store.findViewById(R.id.store);
            TextView coin = one_store.findViewById(R.id.coin);
            TextView p_name = one_store.findViewById(R.id.p_name);

            Button buy = one_store.findViewById(R.id.buy);


            name.setText(names[i]);
            store.setImageResource(images[i]);
            coin.setText(getResources().getString(R.string.cost_coins) + " " + r_c[i]);
            p_name.setText(o_names[i]);

            if (status[i]) {
                buy.setVisibility(GONE);
            } else if (r_c[i] <= all_coin) {
                buy.setBackgroundResource(R.drawable.green_rect);
                buy.setBackgroundColor(getResources().getColor(R.color.white));

                int finalI = i;
                buy.setOnClickListener(View -> {
                    all_coin -= r_c[finalI];

                    editor.putBoolean("store_" + finalI, true);
                    editor.putInt("coin", all_coin);
                    editor.apply();

                    load_store();
                });
            } else {
                buy.setBackgroundResource(R.drawable.dark_rect);
                buy.setBackgroundColor(getResources().getColor(R.color.gray));
            }


            layout_vertical.addView(one_store);
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

//        isMute = sharedPreferences.getBoolean("isMute", false);
//        if (!isMute)
//            Player.all_screens.start();
    }

    @Override
    public void onBackPressed() {
        return;
    }

}