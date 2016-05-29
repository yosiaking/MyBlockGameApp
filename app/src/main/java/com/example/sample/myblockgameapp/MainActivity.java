package com.example.sample.myblockgameapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity  {

    private GameVIew mView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //contextを引数に自作クラスをインスタンス化
        //
        mView = new GameVIew(this, savedInstanceState);
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

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.string.title_activity_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        mView.onSaveInstanceState(outState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState){
        super.onRestoreInstanceState(savedInstanceState);
        mView = new GameVIew(this, savedInstanceState);
        setContentView(mView);
    }
}
