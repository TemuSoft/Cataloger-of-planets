package com.Catalogerofplanets.COP2008;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class AtmosphereView extends View{
    private SharedPreferences sharedPreferences;
    private int screenX, screenY;
    private Resources resources;
    private Random random;
    boolean isPlaying = true, game_over, game_won;
    long game_over_time, game_won_time;
    int duration = 1000;

    int score;
    private int xSpeed, ySpeed;
    private Context context;


    int group_index, item_index;
    Bitmap planet;
    ArrayList<Bitmap> atoms = new ArrayList<>();
    int p_x, p_y, p_wh;
    int padding, at_wh;
    ArrayList<ArrayList<Integer>> atoms_data = new ArrayList<>();
    int[] progress_value = new int[]{0, 0, 0};
    long last_success_time, last_miss_time;
    int failure_index = -1, success_index = -1;
    int miss_counter = 0;


    public AtmosphereView(Context mContext, int scX, int scY, Resources res, int level_amount) {
        super(mContext);
        screenX = scX;
        screenY = scY;
        resources = res;
        context = mContext;
        random = new Random();

        padding = screenX / 10;

        sharedPreferences = context.getSharedPreferences("logerofplanetsCO", context.MODE_PRIVATE);
        group_index = sharedPreferences.getInt("group_index", 0);
        item_index = sharedPreferences.getInt("item_index", 0);

        int index = random.nextInt(3);
        int value = random.nextInt(5) + 1;
        progress_value[index] = value * 10;


        int p = context.getResources().getIdentifier("cha_" + group_index + "_" + item_index, "drawable", context.getPackageName());
        planet = BitmapFactory.decodeResource(res, p);
        p_wh = planet.getWidth();

        at_wh = BitmapFactory.decodeResource(res, R.drawable.img_atm_0).getWidth();
        for (int i = 0; i < 4; i++) {
            int a = context.getResources().getIdentifier("img_atm_" + i, "drawable", context.getPackageName());
            Bitmap bitmap = BitmapFactory.decodeResource(res, a);
            atoms.add(Bitmap.createScaledBitmap(bitmap, at_wh, at_wh, false));

            add_atoms();
        }

        p_x = screenX / 2 - p_wh / 2;
        p_y = padding * 2;

        planet = Bitmap.createScaledBitmap(planet, p_wh, p_wh, false);

        for (int i = 0; i < 4; i++)
            add_atoms();

        setSpeed();
    }

    private void add_atoms() {
        int index = random.nextInt(atoms.size());
        int x = random.nextInt(screenX - padding * 2 - at_wh) + padding;
        int y = random.nextInt(screenY - padding * 2 - at_wh) + padding;

        Rect at = new Rect(x, y, x + at_wh, y + at_wh);
        if (Rect.intersects(at, getPlanetCollision()))
            add_atoms();
        else {
            ArrayList<Integer> data = new ArrayList<>();
            data.add(x);
            data.add(y);
            data.add(index);
            atoms_data.add(data);
        }
    }

    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        canvas.drawColor(Color.TRANSPARENT);

        canvas.drawBitmap(planet, p_x, p_y, paint);
        for (int i = 0; i < atoms_data.size(); i++) {
            int x = atoms_data.get(i).get(0);
            int y = atoms_data.get(i).get(1);
            int index = atoms_data.get(i).get(2);

            if (index == -1)
                continue;

            canvas.drawBitmap(atoms.get(index), x, y, paint);
        }

        if (failure_index != -1) {
            int cx = atoms_data.get(failure_index).get(0) + at_wh / 2;
            int cy = atoms_data.get(failure_index).get(1) + at_wh / 2;

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(getResources().getColor(R.color.red));
            paint.setStrokeWidth(10);
            canvas.drawCircle(cx, cy, at_wh / 2, paint);
        }

    }

    private void setSpeed() {
        xSpeed = screenX / 80;
        ySpeed = screenY / 80;
    }

    public void update() {

        if (last_miss_time != 0 && last_miss_time + 1000 < System.currentTimeMillis()){
            failure_index = -1;
            last_miss_time = 0;
        }

        if (last_success_time != 0 && last_success_time + 1000 < System.currentTimeMillis()) {
            last_success_time = 0;
            int index = random.nextInt(atoms.size());
            atoms_data.get(success_index).set(2, index);
            success_index = -1;
        }

        invalidate();
    }

    private Rect getPlanetCollision() {
        return new Rect(p_x, p_y, p_x + p_wh, p_y + p_wh);
    }

    public void check_game_status() {
        int red = progress_value[0];
        int blue = progress_value[1];
        int green = progress_value[2];

        boolean red_blue = red == 100 && blue == 100;
        boolean red_green = red == 100 && green == 100;
        boolean blue_green = blue == 100 && green == 100;

        if (red_blue || red_green || blue_green) {
            game_won = true;
            game_won_time = System.currentTimeMillis();
        }
    }
}