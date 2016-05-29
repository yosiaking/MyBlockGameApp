package com.example.sample.myblockgameapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

/**
 * Created by apple on 2016/04/19.
 */
public class Ball implements DrawableItem {
    private float mX;
    private float mY;
    private float mSpeedX;
    private float mSpeedY;
    private final float mRadius;

    //初期速度
    private final float mInitialSpeedX;
    private final float mInitialSpeedY;

    //初期位置
    private  final float mInitialX;
    private  final float mInitialY;

    //保存する必要がある情報
    private static final String KEY_X = "x";
    private static final String KEY_Y = "y";
    private static final String KEY_SPEED_X = "speed_x";
    private static final String KEY_SPEED_Y = "speed_y";

    public Ball(float radius, float initialX, float initialY){
        mRadius = radius;
        mSpeedX = radius / 5;
        mSpeedY = radius / 5;
        mX = initialX;
        mY = initialY;
        mInitialSpeedX = mSpeedX;
        mInitialSpeedY = mSpeedY;
        mInitialX = mX;
        mInitialY = mY;
    }

    //描画するボールの図形
    public void draw(Canvas canvas, Paint paint){
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mX, mY, mRadius, paint);
    }

    //ボールを動かす処理
    //呼び出されるたびに、現在の座標+スピード(移動する座標の数)の座標に移動をする
    public void move(){
        mX += mSpeedX;
        mY += mSpeedY;
    }

    public void reset(){
        mX = mInitialX;
        mY = mInitialY;
        mSpeedX = mInitialSpeedX * (float) Math.random() - 0.5f;
        mSpeedY = mInitialSpeedY;
    }

    //保存する情報の格納
    public Bundle save(int width, int height){
        Bundle outState = new Bundle();
        //解像度によって位置は変わるのでそれぞれｘ軸y軸を画面幅で割った「割合」の状態で保存
        outState.putFloat(KEY_X, mX / width);
        outState.putFloat(KEY_Y, mY / height);
        outState.putFloat(KEY_SPEED_X, mSpeedX / width);
        outState.putFloat(KEY_SPEED_Y, mSpeedY / height);
        return outState;
    }

    //保存されたBundleから復元するメソッド
    public void restore(Bundle inState, int width, int height){
        mX = inState.getFloat(KEY_X) * width;
        mY = inState.getFloat(KEY_Y) * height;
        mSpeedX = inState.getFloat(KEY_SPEED_X) * width;
        mSpeedY = inState.getFloat(KEY_SPEED_Y) * height;
    }



    public float getSpeedX(){
        return mSpeedX;
    }

    public float getSpeedY(){
        return mSpeedY;
    }

    public float getY(){
        return mY;
    }

    public float getX(){
        return mX;
    }

    public void setSpeedX(float speedX){
        mSpeedX = speedX;
    }

    public void setSpeedY(float speedY){
        mSpeedY = speedY;
    }



}
