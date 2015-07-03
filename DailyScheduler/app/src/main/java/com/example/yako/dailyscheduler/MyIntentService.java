package com.example.yako.dailyscheduler;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by yako on 7/3/15.
 */
public class MyIntentService extends IntentService {

    public MyIntentService(String name) {
        super(name);
        // TODO 自動生成されたコンストラクター・スタブ
    }

    public MyIntentService(){
        // ActivityのstartService(intent);で呼び出されるコンストラクタはこちら
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // 非同期処理を行うメソッド。タスクはonHandleIntentメソッド内で実行する
        Log.d("IntentService", "onHandleIntent Start");
        Toast.makeText(this, "isChecked", Toast.LENGTH_SHORT).show();
    }


}