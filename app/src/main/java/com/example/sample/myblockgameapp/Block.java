package com.example.sample.myblockgameapp;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

/**
 * Created by apple on 2016/04/19.
 */
public class Block implements DrawableItem {

    private final float mTop;
    private final float mLeft;
    private final float mBottom;
    private final float mRight;
    private int mHard;
    private boolean mIsCollision = false; //衝突状態を記録するフラグ
    private boolean mIsExist = true; //ブロックが破壊されていないか// ;
    private static final String KEY_HARD = "hard";


    public Block(float top, float left, float right, float bottom){
        this.mTop = top;
        this.mLeft = left;
        this.mBottom = bottom;
        this.mRight = right;
        this.mHard = 1;
    }

    public void draw(Canvas canvas, Paint paint){

        //耐久力が0以上の場合のみ
        if(mIsExist){

            //当たり判定
            if(mIsCollision){
                mHard--;
                mIsCollision = false;

                //耐久率が0の場合、処理終わり
                if(mHard <= 0){
                    mIsExist = false;
                    return;
                }
            }


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

    //状態を保存
    public Bundle save(){
        Bundle outState = new Bundle();
        outState.putInt(KEY_HARD, mHard);
        return outState;
    }

    //Bundleから状況を復元する
    public void restore(Bundle inState){
        mHard = inState.getInt(KEY_HARD);
        mIsExist = mHard > 0;
//        if(mHard > 0){
//            mIsExist = true;
//        }else{
//            mIsExist = false;
//        }
        //        以下の様に書き換えも可能
    }

    public void collision(){
        mIsCollision = true; //衝突したかどうかの判定を行い、実際の破壊はdraw()で行う
    }

    public boolean isExist(){
        return mIsExist;
    }

}
