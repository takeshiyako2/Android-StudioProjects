package com.example.yako.dailyscheduler;

import android.app.AlarmManager;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Calendar;
import java.util.TimeZone;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*
        int hour;
        int minuite;
        int service_id;

        hour = 18;
        minuite = 50;

        // 日本(+9)以外のタイムゾーンを使う時はここを変える
        TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");

        //今日の目標時刻のカレンダーインスタンス作成
        Calendar cal_target = Calendar.getInstance();
        cal_target.setTimeZone(tz);
        cal_target.set(Calendar.HOUR_OF_DAY, hour);
        cal_target.set(Calendar.MINUTE, minuite);
        cal_target.set(Calendar.SECOND, 0);

        //現在時刻のカレンダーインスタンス作成
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTimeZone(tz);

        //ミリ秒取得
        long target_ms = cal_target.getTimeInMillis();
        long now_ms = cal_now.getTimeInMillis();

        //今日ならそのまま指定
        if (target_ms >= now_ms) {
            //set(launch_service, target_ms, service_id);
            Log.d("IntentService", String.valueOf(target_ms));
            Toast.makeText(this, String.valueOf(target_ms), Toast.LENGTH_SHORT).show();

            //過ぎていたら明日の同時刻を指定
        } else {
            cal_target.add(Calendar.DAY_OF_MONTH, 1);
            target_ms = cal_target.getTimeInMillis();
            //set(launch_service, target_ms, service_id);
            Log.d("IntentService", String.valueOf(target_ms));
            Toast.makeText(this, String.valueOf(target_ms), Toast.LENGTH_SHORT).show();
        }
*/



        long target_ms = get_time_by_hour_minuite(18, 50);
        Log.d("IntentService", String.valueOf(target_ms));
        Toast.makeText(this, String.valueOf(target_ms), Toast.LENGTH_SHORT).show();


    }


    public long get_time_by_hour_minuite(int hour, int minuite) {

        // 日本(+9)以外のタイムゾーンを使う時はここを変える
        TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");

        //今日の目標時刻のカレンダーインスタンス作成
        Calendar cal_target = Calendar.getInstance();
        cal_target.setTimeZone(tz);
        cal_target.set(Calendar.HOUR_OF_DAY, hour);
        cal_target.set(Calendar.MINUTE, minuite);
        cal_target.set(Calendar.SECOND, 0);

        //現在時刻のカレンダーインスタンス作成
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTimeZone(tz);

        //ミリ秒取得
        long target_ms = cal_target.getTimeInMillis();
        long now_ms = cal_now.getTimeInMillis();

        //今日ならそのまま指定
        if (target_ms >= now_ms) {
            //過ぎていたら明日の同時刻を指定
        } else {
            cal_target.add(Calendar.DAY_OF_MONTH, 1);
            target_ms = cal_target.getTimeInMillis();
        }

        return target_ms;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
