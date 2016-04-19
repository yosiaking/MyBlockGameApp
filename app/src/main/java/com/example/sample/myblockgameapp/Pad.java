package com.example.sample.myblockgameapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by apple on 2016/04/19.
 */
public class Pad implements DrawableItem {
    private final float mTop;
    private float mLeft;
    private float mRight;
    private final float mBottom;

    public Pad(float top, float bottom){
        this.mTop = top;
        this.mBottom = bottom;
    }

    public void setLeftRight(float left, float right){
        mLeft = left;
        mRight = right;
    }

    public void draw(Canvas canvas, Paint paint){
        paint.setColor(Color.parseColor("#bbbbbb"));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(mLeft, mTop, mRight, mBottom, paint);
    }
}
