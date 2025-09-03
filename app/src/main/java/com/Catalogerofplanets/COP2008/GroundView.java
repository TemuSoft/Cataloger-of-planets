package com.Catalogerofplanets.COP2008;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GroundView extends View {
    private SharedPreferences sharedPreferences;
    private int screenX, screenY;
    private Resources resources;
    private Random random;
    boolean isPlaying = true, game_over, game_won, show_time_over = false;
    long game_over_time, game_won_time;
    int duration = 1000;
    long start_time = System.currentTimeMillis();
    long pause_time;

    int score;
    private int xSpeed, ySpeed;
    private Context context;


    int group_index, item_index;
    int padding, margin;
    Bitmap ground_rect, ground_eclipse;
    ArrayList<Bitmap> soils = new ArrayList<>();
    int gr_w, gr_h, ge_w, ge_h;
    int init_x_gr, init_y_gr;
    int init_x_ge, init_y_ge;

    int s_w, s_h;
    ArrayList<ArrayList<Integer>> rect_data = new ArrayList<>();
    ArrayList<ArrayList<Integer>> eclipse_data = new ArrayList<>();

    int tap_x = -1, tap_y = -1, tap_index = -1;
    boolean move_is_internal;


    public GroundView(Context mContext, int scX, int scY, Resources res, int level_amount) {
        super(mContext);
        screenX = scX;
        screenY = scY;
        resources = res;
        context = mContext;
        random = new Random();

        padding = screenX / 10;
        margin = padding / 2;

        sharedPreferences = context.getSharedPreferences("logerofplanetsCO", context.MODE_PRIVATE);
        group_index = sharedPreferences.getInt("group_index", 0);
        item_index = sharedPreferences.getInt("item_index", 0);

        ground_rect = BitmapFactory.decodeResource(res, R.drawable.ground_rect);
        ground_eclipse = BitmapFactory.decodeResource(res, R.drawable.ground_eclipse);

        int w = ground_eclipse.getWidth();
        int h = ground_eclipse.getHeight();
        ge_w = (screenX - padding * 2 - margin * 3) / 4;
        ge_h = ge_w * h / w;

        w = ground_rect.getWidth();
        h = ground_rect.getHeight();
        gr_h = (screenY - padding * 2 - margin * 4 - ge_h) / 4;
        gr_w = gr_h * w / h;

        init_x_gr = screenX / 2 - gr_w / 2;
        init_y_gr = padding;

        init_x_ge = padding;
        init_y_ge = screenY - padding - ge_h;

        ground_rect = Bitmap.createScaledBitmap(ground_rect, gr_w, gr_h, false);
        ground_eclipse = Bitmap.createScaledBitmap(ground_eclipse, ge_w, ge_h, false);

        w = BitmapFactory.decodeResource(res, R.drawable.soil_0).getWidth();
        h = BitmapFactory.decodeResource(res, R.drawable.soil_0).getHeight();

        if (s_h > h) s_w = w * s_h / h;

        if (s_w > w) {
            s_h = h * s_w / w;
            s_w = w;
        } else {
            s_w = w;
            s_h = h;
        }

        for (int i = 0; i < 4; i++) {
            int s = context.getResources().getIdentifier("soil_" + i, "drawable", context.getPackageName());
            Bitmap bitmap = BitmapFactory.decodeResource(res, s);
            soils.add(Bitmap.createScaledBitmap(bitmap, s_w, s_h, false));
        }

        setSpeed();

        ArrayList<Integer> data = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            data.add(i);
        Collections.shuffle(data);
        Collections.shuffle(data);

        for (int i = 0; i < data.size(); i++)
            initialize_data(data.get(i));
    }

    private void initialize_data(Integer index) {
        int s = rect_data.size();
        int rx = init_x_gr;
        int ry = init_y_gr + (margin + gr_h) * (s - 1);

        int x = rx + gr_w / 2 - s_w / 2;
        int y = ry + gr_h / 2 - s_h / 2;

        ArrayList<Integer> data = new ArrayList<>();
        data.add(rx);
        data.add(ry);

        // origin data
        data.add(x);
        data.add(y);
        data.add(index);

        // will update on play
        data.add(x);
        data.add(y);
        data.add(index);
        rect_data.add(data);


        int ex = init_x_ge + (margin + ge_w) * (s - 1);
        int ey = init_y_ge;

        x = ex + ge_w / 2 - s_w / 2;
        y = ey + ge_h / 2 - s_h / 2;

        data = new ArrayList<>();
        data.add(ex);
        data.add(ey);

        // origin data
        data.add(x);
        data.add(y);
        data.add(index);

        // will update on play
        data.add(x);
        data.add(y);
        data.add(index);
        eclipse_data.add(data);

    }


    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        canvas.drawColor(Color.TRANSPARENT);


        for (int i = 0; i < rect_data.size(); i++) {
            int rx = rect_data.get(i).get(0);
            int ry = rect_data.get(i).get(1);
            int x = rect_data.get(i).get(2);
            int y = rect_data.get(i).get(3);
            int index = rect_data.get(i).get(4);
            int ux = rect_data.get(i).get(5);
            int uy = rect_data.get(i).get(6);
            int uindex = rect_data.get(i).get(7);

            canvas.drawBitmap(ground_rect, rx, ry, paint);

            if (uindex == -1) continue;

            canvas.drawBitmap(soils.get(uindex), ux, uy, paint);
        }

        if (show_time_over) {
            for (int i = 0; i < eclipse_data.size(); i++) {
                int ex = eclipse_data.get(i).get(0);
                int ey = eclipse_data.get(i).get(1);
                int x = eclipse_data.get(i).get(2);
                int y = eclipse_data.get(i).get(3);
                int index = eclipse_data.get(i).get(4);
                int ux = eclipse_data.get(i).get(5);
                int uy = eclipse_data.get(i).get(6);
                int uindex = eclipse_data.get(i).get(7);

                canvas.drawBitmap(ground_rect, ex, ey, paint);

                if (uindex == -1) continue;

                canvas.drawBitmap(soils.get(uindex), ux, uy, paint);
            }
        }
    }

    private void setSpeed() {
        xSpeed = screenX / 80;
        ySpeed = screenY / 80;
    }

    public void update() {

        if (!show_time_over) {
            if (start_time + 5000 < System.currentTimeMillis()) {
                show_time_over = true;

                for (int i = 0; i < rect_data.size(); i++) {
                    rect_data.get(i).set(7, -1);
                }
            }
        }

        invalidate();
    }

    public void check_game_status() {
        if (show_time_over) {
            boolean has_empty = false;
            boolean index_matched = true;
            for (int i = 0; i < rect_data.size(); i++) {
                int index = rect_data.get(i).get(4);
                int uindex = rect_data.get(i).get(7);

                if (uindex == -1) has_empty = true;

                if (index != uindex) index_matched = false;
            }

            if (!has_empty) {
                if (index_matched) {
                    game_won = true;
                    game_won_time = System.currentTimeMillis();
                } else {
                    game_over = true;
                    game_won_time = System.currentTimeMillis();
                }
            }
        }
    }
}