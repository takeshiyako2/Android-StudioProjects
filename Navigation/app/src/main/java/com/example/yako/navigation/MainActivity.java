package com.example.yako.navigation;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebViewFragment;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;
import java.util.TimeZone;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private static String TAG = "MainActivity";
    private static Integer THIS_POSITION = 0;


    TimePicker tPicker;
    int notificationId;
    private PendingIntent alarmIntent;
    private String nextalarm;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ナビゲーション
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // アラームの時間設定
        int hour = 0;
        int minute = 0;
        long alarmStartTime = get_time_by_hour_minuite(hour, minute);
        Log.d("IntentService", String.valueOf(alarmStartTime));

        // アラームの時間設定 デバッグ用（本番時にはコメントアウトしておく）
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.MINUTE, minute);
        startTime.set(Calendar.SECOND, 0);
        alarmStartTime = startTime.getTimeInMillis();

        // アラームセット
        Intent bootIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        bootIntent.putExtra("notificationId", notificationId);
        alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, bootIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        // リピート
        alarm.setRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmStartTime,
                1 * 1000 * 60,
                alarmIntent
        );
//        Toast.makeText(MainActivity.this, "通知セット完了!", Toast.LENGTH_SHORT).show();
        notificationId++;

    }


    // 次のアラームの時刻を取得
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
    public void onNavigationDrawerItemSelected(int position) {
        // 戻るボタンのためポジションをとっておく
        THIS_POSITION = position;

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        if(position == 0){
            fragmentManager.beginTransaction()
                    .replace(R.id.container, Web1Fragment.newInstance(position + 1))
                    .commit();
        }
        else if(position == 1){
            fragmentManager.beginTransaction()
                    .replace(R.id.container, Web2Fragment.newInstance(position + 1))
                    .commit();
        }
        else if(position == 2){
            fragmentManager.beginTransaction()
                    .replace(R.id.container, Web3Fragment.newInstance(position + 1))
                    .commit();
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    // アクションバーを押したとき
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.d(TAG, "onOptionsItemSelected 1");

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_home:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.action_rotate:
                startActivity(new Intent(this, MainActivity.class));
                return true;
        }
        return false;
    }


    // 戻るボタン
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed 1");
        Log.d(TAG, String.valueOf(THIS_POSITION));

        // 各Fragmentを起動
        Integer position = THIS_POSITION;
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (position == 0) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, Web1Fragment.newInstance(position + 1))
                    .commit();
        } else if (position == 1) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, Web2Fragment.newInstance(position + 1))
                    .commit();
        } else if (position == 2) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, Web3Fragment.newInstance(position + 1))
                    .commit();
        } else {
            // トップを起動
            finish();
            Intent i = new Intent(MainActivity.this, MainActivity.class);
            startActivity(i);
        }

    }



}
