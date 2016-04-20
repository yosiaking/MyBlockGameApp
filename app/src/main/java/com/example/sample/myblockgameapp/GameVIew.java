package com.example.sample.myblockgameapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

    //padのメンバ変数
    private Pad mPad;
    private float mPadHalfWidth;

    //ボールのメンバ変数
    private Ball mBall;
    private float mBallRadius;

    //スレッドのメンバ変数
    private Thread mThread;
    private ArrayList<DrawableItem> mItemList;

    //残ったブロックの個数
    private ArrayList<Block> mBlockList;

    //ブロックの幅と高さ
    private float mBlockWidth;
    private float mBlockHeight;


    //ブロックの個数
    private final int BLOCK_COUNT = 100;

    //ライフ
    private int mLife;

    //ゲームを開始した時の時間
    private long mGameStartTime;

    //UI ThreadでActivityを起動
    //Handlerで行いたい処理を別スレッドに渡すことで、渡されたスレッドは渡されたHandlerを順番に処理していく
    private Handler mHandler;



    public GameVIew(final Context context) {
        //StartActivityはContextクラスから受け取っている
        //そのため、GameViewクラスでは使えないが、Contextクラスを継承したコンストラクタなら可能
        super(context);
        //図形を描写する処理
        setSurfaceTextureListener(this);
        setOnTouchListener(this);

        mHandler = new Handler(){
            public void handleMessage(Message message){
                //コンストラクタがIntentを行うため、contextを第1引数にする
                Intent intent = new Intent(context, ClearActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtras(message.getData());
                context.startActivity(intent);
            }
        };
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

                //trueの限り、中の処理を行い続ける
                //※だから、タッチする度に位置が変わったりする
                while(true){

                    //端末ごとのループ処理の最適化(startTimeを取得)
                    long startTime = System.currentTimeMillis();

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

                        //画面をタッチするたびにTouchListenerが呼び出され、位置が変わる
                        float padLeft = mTouchedX - mPadHalfWidth;
                        float padRight = mTouchedX + mPadHalfWidth;
                        mPad.setLeftRight(padLeft, padRight);

                        //ボールの移動
                        mBall.move();
                        float ballTop = mBall.getY() - mBallRadius;
                        float ballLeft = mBall.getX() - mBallRadius;
                        float ballBottom = mBall.getY() + mBallRadius;
                        float ballRight = mBall.getX() + mBallRadius;


                        //ボールの左側が0(左の壁にぶつかる)&speedXが0以下(左に向かって移動している)
                        //もしくは、ボールの右側がgetWidth幅(右の壁にぶつかる)&speedXが0以上(右に向かって移動している)
                        if(ballLeft < 0 && mBall.getSpeedX() < 0 || ballRight >= getWidth() && mBall.getSpeedX() > 0){
                            //横の壁にぶつかったので速度を反転
                            mBall.setSpeedX(-mBall.getSpeedX()); //
                        }

                        if(ballTop < 0){
                            //横の壁にぶつかったので速度を反転
                            mBall.setSpeedY(-mBall.getSpeedY()); //
                        }

                        if(ballTop > getHeight()){
                            if(mLife>0){
                                mLife--;
                                mBall.reset();
                            }else{
                                unlockCanvasAndPost(canvas);
                                Message message = Message.obtain();
                                Bundle bundle = new Bundle();
                                bundle.putBoolean(ClearActivity.EXTRA_IS_CLEAR, false);
                                bundle.putInt(ClearActivity.EXTRA_BLOCK_COUNT, getBlockCount());
                                bundle.putLong(ClearActivity.EXTRA_TIME, System.currentTimeMillis() - mGameStartTime);
                                message.setData(bundle);
                                mHandler.sendMessage(message);
                                return;
                            }
                        }

                        //ブロックの現在地ザ行を取得
                        Block leftBlock = getBlock(ballLeft, mBall.getY());
                        Block topBlock = getBlock(mBall.getX(), ballTop);
                        Block rightBlock = getBlock(ballRight, mBall.getY());
                        Block bottomBlock = getBlock(mBall.getX(), ballBottom);

                        //ゲームをクリアしたかの判定
                        boolean isCollision = false;

                        //ボールが存在したら衝突処理
                        //左側
                        if(leftBlock != null){
                            mBall.setSpeedX(-mBall.getSpeedX());
                            leftBlock.collision();
                            isCollision = true;
                        }
                        //上側
                        if(topBlock != null){
                            mBall.setSpeedY(-mBall.getSpeedY());
                            topBlock.collision();
                            isCollision = true;
                        }
                        //右側
                        if(rightBlock != null){
                            mBall.setSpeedX(-mBall.getSpeedX());
                            rightBlock.collision();
                            isCollision = true;
                        }
                        //下側
                        if(bottomBlock != null){
                            mBall.setSpeedY(-mBall.getSpeedY());
                            bottomBlock.collision();
                            isCollision = true;
                        }


                        //パッドとボールの衝突判定処理
                        float padTop = mPad.getTop();
                        float ballSpeedY = mBall.getSpeedY();

                        //バットとボールが衝突したかを判定するif文の条件
                        if(ballBottom > padTop && ballBottom - ballSpeedY < padTop && padLeft < ballRight && padRight > ballLeft)
                        {
                            if(ballSpeedY < mBlockHeight / 3){
                                ballSpeedY *= -1.05;
                            }else{
                                ballSpeedY = -ballSpeedY;
                            }

                            float ballSpeedX = mBall.getSpeedX() + (mBall.getX() - mTouchedX) / 10;

                            if(ballSpeedX > mBlockWidth / 5){
                                ballSpeedX = mBlockWidth / 5;
                            }

                            mBall.setSpeedY(ballSpeedY);
                            mBall.setSpeedX(ballSpeedX);
                        }


                        //オブジェクト(DrawableItemの実装クラス)の描画処理
                        for(DrawableItem item : mItemList){
                            item.draw(canvas, paint);
                        }
                        //canvasのロックを解除
                        unlockCanvasAndPost(canvas);

                        //キャンバスがロックされたままにならないように、ロックが解除されたあとで処理を実行
                        //ブロックが壊れた&ブロックのカウントがゼロになった場合
                        if(isCollision && getBlockCount() == 0){
                            Message message = Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(ClearActivity.EXTRA_IS_CLEAR, true);
                            bundle.putInt(ClearActivity.EXTRA_BLOCK_COUNT, 0);
                            bundle.putLong(ClearActivity.EXTRA_TIME, System.currentTimeMillis() - mGameStartTime);
                            message.setData(bundle);
                            mHandler.sendMessage(message);
                        }

                    }

                    //ループ処理の最適化(sleepTime取得)
                    long sleepTime = 16 - System.currentTimeMillis() + startTime;
                    //もし1ミリ以上でもあれば 16ミリ秒までスレッドを止める
                    if(sleepTime > 0){
                        try{
                            Thread.sleep(sleepTime);
                        }catch (Exception e){
                        }

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

        mBlockWidth = width / 10;
        mBlockHeight = height / 20;

        //
        mBlockList = new ArrayList<Block>();
        for(int i = 0; i < BLOCK_COUNT; i++){
            float blockTop = i / 10 * mBlockHeight;
            float blockLeft = i % 10 * mBlockWidth;
            float blockRight = blockLeft + mBlockWidth;
            float blockBottom = blockTop + mBlockHeight;
            mBlockList.add(new Block(blockTop, blockLeft, blockRight, blockBottom));
        }

        //mBlockList配列の値をすべてmItemListに入れる
        mItemList = new ArrayList<DrawableItem>();
        mItemList.addAll(mBlockList);

        mPad = new Pad(height * 0.8f, height * 0.85f);
        mItemList.add(mPad);
        mPadHalfWidth = width / 10;

        //ボールの大きさを定義してインスタンス化
        mBallRadius = width < height ? width / 40 : height / 40 ;
        mBall = new Ball(mBallRadius, width/2, height/2);
        mItemList.add(mBall);

        //ライフの初期値
        mLife = 5;

        //開始時間
        mGameStartTime = System.currentTimeMillis();


    }

    private int getBlockCount(){
        int count = 0;
        for(Block block :mBlockList){
            if(block.isExist()){
                count++;
            }
        }
        return count;
    }

    //特定の座標にあるブロックを取得するメソッド
    private Block getBlock(float x, float y){
        int index = (int)(x / mBlockWidth) + (int)(y / mBlockHeight) * 10;
        if(0 <= index && index < BLOCK_COUNT){
            Block block = (Block)mItemList.get(index);
            if(block.isExist()){
                return block;
            }
        }
        return null;
    }


}
