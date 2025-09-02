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

    public AtmosphereView(Context mContext, int scX, int scY, Resources res, int level_amount) {
        super(mContext);
        screenX = scX;
        screenY = scY;
        resources = res;
        context = mContext;
        random = new Random();


        setSpeed();
    }

    public void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        canvas.drawColor(Color.TRANSPARENT);



    }

    private void setSpeed() {
        xSpeed = screenX / 80;
        ySpeed = screenY / 80;
    }

    public void update() {

        invalidate();
    }
}