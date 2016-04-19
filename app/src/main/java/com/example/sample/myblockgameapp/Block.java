package com.example.sample.myblockgameapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by apple on 2016/04/19.
 */
public class Block implements DrawableItem {

    private final float mTop;
    private final float mLeft;
    private final float mBottom;
    private final float mRight;
    private int mHard;

    public Block(float top, float left, float right, float bottom){
        this.mTop = top;
        this.mLeft = left;
        this.mBottom = bottom;
        this.mRight = right;
        this.mHard = 1;
    }

    public void draw(Canvas canvas, Paint paint){
        if(mHard > 0){
            //塗りつぶし部分を描画
            paint.setColor(Color.parseColor("#4444ff"));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(mLeft, mTop, mRight, mBottom, paint);
            //枠線部分を描画
            paint.setColor(Color.parseColor("#000000"));
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(mLeft, mTop, mRight, mBottom, paint);

        }
    }

}
