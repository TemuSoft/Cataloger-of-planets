package com.Catalogerofplanets.COP2008;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class CoreView extends View {
    private SharedPreferences sharedPreferences;
    private int screenX, screenY;
    private Resources resources;
    private Random random;
    boolean isPlaying = true, game_over, game_won;
    long game_over_time, game_won_time;
    int duration = 1000;
    long start_time = System.currentTimeMillis();
    long pause_time;

    int score;
    private int xSpeed, ySpeed;
    private Context context;


    int playLevel, item_index;
    int padding, margin, gap;

    Bitmap core, click, progress_outer, progress_inner;
    int c_x, c_y, c_w_h;
    int cl_w_h;
    int pro_l_x, pro_l_y, pro_l_w, pro_l_h;
    int pro_s_x, pro_s_y, pro_s_w, pro_s_h;
    int line_x, line_y;
    int init_x, init_y;
    ArrayList<ArrayList<Integer>> click_data = new ArrayList<>();
    int expected_value = 0, actual_value = 0;
    int[] values = new int[]{10, 15, 20, 25, 30};


    public CoreView(Context mContext, int scX, int scY, Resources res, int level_amount) {
        super(mContext);
        screenX = scX;
        screenY = scY;
        resources = res;
        context = mContext;
        random = new Random();

        padding = screenX / 10;
        margin = padding / 2;
        int amount = random.nextInt(values.length - 2) + 1;
        ArrayList<Integer> data = new ArrayList<>();
        for (int i = 0; i < values.length; i++)
            data.add(values[i]);
        Collections.shuffle(data);
        Collections.shuffle(data);
        Collections.shuffle(data);

        for (int i = 0; i < amount; i++)
            expected_value += data.get(i);

        sharedPreferences = context.getSharedPreferences("logerofplanetsCO", context.MODE_PRIVATE);
        playLevel = sharedPreferences.getInt("playLevel", 0);
        item_index = sharedPreferences.getInt("item_index", 0);

        core = BitmapFactory.decodeResource(res, R.drawable.planet_core);
        click = BitmapFactory.decodeResource(res, R.drawable.click);
        progress_outer = BitmapFactory.decodeResource(res, R.drawable.progress_vertical);
        progress_inner = BitmapFactory.decodeResource(res, R.drawable.progress_inner);

        c_w_h = screenX * 2 / 3;
        gap = c_w_h / 12;
        cl_w_h = gap * 3 / 2;

        c_x = screenX / 2 - c_w_h / 2;
        c_y = padding;

        pro_l_w = cl_w_h * 4 / 5;
        pro_l_h = c_w_h;
        pro_l_x = padding;
        pro_l_y = c_x;

        int gp = pro_l_w / 5;
        pro_s_w = pro_l_w - gp * 2;
        pro_s_h = pro_s_h - gp * 2;
        pro_s_x = pro_l_x + gp;
        pro_s_y = pro_l_y + gp;

        init_x = pro_s_x;
        init_y = pro_s_y + pro_s_h;

        line_x = pro_s_x;
        line_y = init_y - pro_s_h * expected_value / 100;

        core = Bitmap.createScaledBitmap(core, c_w_h, c_w_h, false);
        click = Bitmap.createScaledBitmap(click, cl_w_h, cl_w_h, false);
        progress_outer = Bitmap.createScaledBitmap(progress_outer, pro_l_w, pro_l_h, false);
        progress_inner = Bitmap.createScaledBitmap(progress_inner, pro_s_w, pro_s_h, false);

        setSpeed();
    }

    public void add_click(int x, int y) {
        int[] radios = new int[]{gap * 6, gap * 5, gap * 4, gap * 3, gap * 2};
        int dx = x - (c_x + c_w_h / 2);
        int dy = y - (c_y + c_w_h / 2);
        int value = 0;
        for (int i = 0; i < radios.length; i++) {
            int r = radios[i];
            boolean isInside = (dx * dx + dy * dy) <= (r * r);
            if (isInside) {
                value = values[i];
                break;
            }
        }

        if (value > 0) {
            ArrayList<Integer> data = new ArrayList<>();
            data.add(x - cl_w_h / 2);
            data.add(y - cl_w_h / 2);
            data.add(value);
            click_data.add(data);

            actual_value += value;

            check_game_status();
        }
    }

    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        canvas.drawColor(Color.TRANSPARENT);

        canvas.drawBitmap(core, c_x, c_y, paint);

        for (int i = 0; i < click_data.size(); i++) {
            int x = click_data.get(i).get(0);
            int y = click_data.get(i).get(1);

            canvas.drawBitmap(click, x, y, paint);
        }

        canvas.drawBitmap(progress_outer, pro_l_x, pro_l_y, paint);
        canvas.drawBitmap(progress_inner, pro_s_x, pro_s_y, paint);
        paint.setColor(getResources().getColor(R.color.blue));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(line_x, line_y, line_x + pro_s_w, line_y + 3, paint);

        canvas.drawCircle(c_x, c_y, gap * 2, paint);
        canvas.drawCircle(c_x, c_y, gap * 3, paint);
        canvas.drawCircle(c_x, c_y, gap * 4, paint);
        canvas.drawCircle(c_x, c_y, gap * 5, paint);
        canvas.drawCircle(c_x, c_y, gap * 6, paint);

        paint.setColor(getResources().getColor(R.color.green));
        paint.setStyle(Paint.Style.FILL);
        int h = actual_value * pro_s_h / expected_value;
        Path path = getEllipseBar(pro_s_w, h, init_x, init_y);
        canvas.drawPath(path, paint);
    }

    private void setSpeed() {
        xSpeed = screenX / 80;
        ySpeed = screenY / 80;
    }

    public void update() {

        invalidate();
    }

    private Path getEllipseBar(int width, int height, int initX, int initY) {
        Path path = new Path();
        path.moveTo(initX, initY);

        initY += height;
        path.lineTo(initX, initY);

        for (int i = 0; i < 180; i += 10) {
            int xy[] = xyPrimePoint(initX, initY, i, initX, initY + height / 2);
            path.lineTo(xy[0], xy[1]);
        }
        initX += width;
        initY -= height;
        path.lineTo(initX, initY);


        for (int i = 0; i < 180; i += 10) {
            int xy[] = xyPrimePoint(initX, initY, i, initX, initY - height / 2);
            path.lineTo(xy[0], xy[1]);
        }
        path.lineTo(initX, initY);

        return path;
    }


    public int[] xyPrimePoint(int x, int y, int angle, int cx, int cy) {
        double radians = Math.toRadians(angle);

        int x1 = x - cx;
        int y1 = y - cy;
        int x2 = (int) (x1 * Math.cos(radians) - y1 * Math.sin(radians));
        int y2 = (int) (x1 * Math.sin(radians) + y1 * Math.cos(radians));

        x = x2 + cx;
        y = y2 + cy;

        return new int[]{x, y};
    }


    public void check_game_status() {
        if (actual_value > expected_value) {
            game_over = true;
            game_over_time = System.currentTimeMillis();
            invalidate();
        } else if (actual_value == expected_value) {
            game_won = true;
            game_won_time = System.currentTimeMillis();
            invalidate();
        }
    }
}