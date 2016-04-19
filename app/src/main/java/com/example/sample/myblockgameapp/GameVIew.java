package com.example.sample.myblockgameapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by apple on 2016/04/19.
 */
public class GameVIew extends TextureView implements TextureView.SurfaceTextureListener,View.OnTouchListener {

    //volatile
    //Javaでは高速化のために、volatileを指定していない変数について、
    //「Thread内で変数を更新しなかった場合」変数の値は更新されていないと判断して初期の値をそのまま参照することがある
    //volatileを指定することで最適化処理を無効にする
    //別のThreadで更新した変数の値を読み取るには変数にvolatileをつける
    volatile private boolean mIsRunnable;

    volatile private float mTouchedX;
    volatile private float mTouchedY;

    private Pad mPad;
    private float mPadHalfWidth;

    private Thread mThread;
    private ArrayList<DrawableItem> mItemList;


    public GameVIew(Context context) {
        super(context);
        //図形を描写する処理
        setSurfaceTextureListener(this);
        setOnTouchListener(this);
    }



    //一度終了したThreadは再度start()を呼んでも実行されることはない
    //そのためThreadを作る処理をコンストラクタからstart()内に入れて、start()のたびに新しいThreadが作られるようにする。
    public void start(){
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Paint paint = new Paint();
                paint.setColor(Color.parseColor("#ff3333"));
                paint.setStyle(Paint.Style.FILL);

                while(true){
                    synchronized (this){
                        if(!mIsRunnable){
                            break;
                        }

                        //Canvasをlockする
                        Canvas canvas = lockCanvas();
                        //lockできなかった場合はcontinueを呼ぶ
                        if(canvas == null){
                            continue;
                        }
                        canvas.drawColor(Color.parseColor("#000000"));

                        float padLeft = mTouchedX - mPadHalfWidth;
                        float padRight = mTouchedX + mPadHalfWidth;
                        mPad.setLeftRight(padLeft, padRight);
//                        mPad.draw(canvas, paint);

                        for(DrawableItem item : mItemList){
                            item.draw(canvas, paint);
                        }
                        unlockCanvasAndPost(canvas);
                    }
                }
            }
        });
        mIsRunnable = true;
        mThread.start();
    }


    public void stop(){
        mIsRunnable = false;
    }

    public boolean onTouch(View v, MotionEvent event){
        mTouchedX = event.getX();
        mTouchedY = event.getY();
        return true;
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        readyObjects(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        readyObjects(width, height);
    }

    @Override
    //SurfaceTextureが破棄するときに呼び出される
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        //Canvasをロックして描画中は、onSurfaceTextureDestroyedが完了せず、Canvasが削除されなくなる
        synchronized (this){
            //falseの場合はリソースの破棄をプログラマーが行う必要がある
            //特にカスタマイズする必要がないので、true
            return true;
        }
    }


    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    public void readyObjects(int width, int height){

        float blockWidth = width / 10;
        float blockHeight = height / 20;
        
        mItemList = new ArrayList<DrawableItem>();
        for(int i = 0; i < 100; i++){
            float blockTop = i / 10 * blockHeight;
            float blockLeft = i % 10 * blockWidth;
            float blockRight = blockLeft + blockWidth;
            float blockBottom = blockTop + blockHeight;
            mItemList.add(new Block(blockTop, blockLeft, blockRight, blockBottom));
        }

        mPad = new Pad(height * 0.8f, height * 0.85f);
        mItemList.add(mPad);
        mPadHalfWidth = width / 10;
    }

}
