package com.example.sample.myblockgameapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private GameVIew mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //contextを引数に自作クラスをインスタンス化
        mView = new GameVIew(this);
        setContentView(mView);
    }

    protected void onResume(){
        super.onResume();
        //スレッドの作成&開始
        mView.start();
    }

    protected void onPause(){
        super.onPause();
        mView.stop();
    }
}
