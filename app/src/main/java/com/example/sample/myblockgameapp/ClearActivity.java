package com.example.sample.myblockgameapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ClearActivity extends AppCompatActivity {

    public static final String EXTRA_IS_CLEAR = "com.example.sample.myblockgameapp_IS_CLEAR";
    public static final String EXTRA_BLOCK_COUNT = "com.example.sample.myblockgameapp_IS_BLOCKCOUNT";
    public static final String EXTRA_TIME = "com.example.sample.myblockgameapp_IS_EXTRATIME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear);

        Intent receiveIntent = getIntent();
        if(receiveIntent == null){
            finish();
        }

        Bundle receiveExtras = receiveIntent.getExtras();
        if(receiveExtras == null){
            finish();
        }

        boolean isClear = receiveExtras.getBoolean(EXTRA_IS_CLEAR, false);
        int blockCount = receiveExtras.getInt(EXTRA_BLOCK_COUNT, 0);
        long clearTime = receiveExtras.getLong(EXTRA_TIME, 0);

        TextView textTitle = (TextView) findViewById(R.id.titleText);
        TextView textBlockCount = (TextView) findViewById(R.id.textBlockCount);
        TextView textClearTime = (TextView) findViewById(R.id.textClearTime);
        Button gameStart = (Button) findViewById(R.id.restartButton);

        //結果
        if(isClear){
            textTitle.setText("Clear!!");
        }else{
            textTitle.setText("GameOver");
        }

        //残ったブロックの数
        textBlockCount.setText(getString(R.string.blockCount, blockCount));

        //クリア、またはゲームオーバー時の時間
        textClearTime.setText(getString(R.string.time, clearTime / 1000, clearTime % 10));

        //ボタン押下時
        gameStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClearActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });

        TextView textScore = (TextView) findViewById(R.id.textScore);
        final long score = (GameVIew.BLOCK_COUNT - blockCount) * (600-(clearTime / 1000)) ;
        textScore.setText(getString(R.string.score, score));

        //ハイスコアの保存&所得

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        long highScore = sharedPreferences.getLong("high_score", 0);
        //ハイスコアが更新された場合の処理の追加
        if(highScore < score){
            highScore = score;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //putで値を保存
            editor.putLong("high_score", highScore);
            editor.commit();
        }

        TextView textHighScore =  (TextView) findViewById(R.id.textHighScore);
        textHighScore.setText(getString(R.string.high_score, highScore));




    }
}
