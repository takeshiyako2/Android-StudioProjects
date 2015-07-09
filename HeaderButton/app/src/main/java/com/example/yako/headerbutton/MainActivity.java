package com.example.yako.headerbutton;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private static String TAG = "MainActivity";
    private WebView mWebView;
    private static String top_url = "http://9post.jp/";
    private static String menu1_url = "http://9post.jp/";
    private static String menu2_url = "http://9post.jp/ranking";
//    private static String top_url = "https://www.google.co.jp/";
//    private static String menu1_url = "https://www.google.co.jp/";
//    private static String menu2_url = "https://www.facebook.com/";


    private Button button1;
    private Button button2;
    TimePicker tPicker;
    int notificationId;
    private PendingIntent alarmIntent;

    /** エラーページ */
    private View mErrorPage;

    /** ページ取得失敗判定 */
    private boolean mIsFailure = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 通知から開いたときの処理 top_urlをmenu1_urlにする
        String keyword = getIntent().getStringExtra("Notification");
        if (!TextUtils.isEmpty(keyword)) {
            // keyword がある場合の処理
            Log.d(TAG, keyword);
            top_url = menu1_url;
        }

        // アイコン
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(R.drawable.nine_post_icon_small);

        // WebViewの設定
        mWebView = (WebView) findViewById(R.id.webView1);
        mWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String ua = mWebView.getSettings().getUserAgentString();
        ua = ua + " 9post-android";
        mWebView.getSettings().setUserAgentString(ua);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.loadUrl(top_url);

        // シェアボタンのリスナー設定
        button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(this);
        button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(this);

        // アラームの時間設定
        int hour = 0;
        int minute = 0;
        long alarmStartTime = get_time_by_hour_minuite(hour, minute);
        Log.d(TAG, "IntentService" + " " + String.valueOf(alarmStartTime));

        // アラームの時間設定 デバッグ用（本番時にはコメントアウトしておく）
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.MINUTE, minute);
        startTime.set(Calendar.SECOND, 0);
        alarmStartTime = startTime.getTimeInMillis();

        // アラームセット
        Intent bootIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        bootIntent.putExtra("notificationId", notificationId);
        alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 102, bootIntent, PendingIntent.FLAG_CANCEL_CURRENT);
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

    /***
     * シェアボタンを押したとき
     */
    public void onClick(View view){
        Log.d(TAG, "onClick");
        String url = mWebView.getUrl();
        switch (view.getId()) {
            case R.id.button1:
                share("com.facebook.katana", url);
                break;
            case R.id.button2:
                String title = mWebView.getTitle();
                share("com.twitter.android", title + " " + url);
                break;
        }
    }

    // シェア用
    private void share(String packageName, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.setPackage(packageName);   // パッケージをそのまま指定
        try {
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException e) {
            // 指定パッケージのアプリがインストールされていないか
            // ACTION_SENDに対応していないか
            Toast.makeText(this, "not installed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /***
     * アクションバーを押したとき
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        finish(); // アクティビティスタックを破棄
        switch (item.getItemId()) {
            case R.id.menu1:
                top_url = menu1_url;
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.menu2:
                top_url = menu2_url;
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***
     * 戻るボタン
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        finish();
    }

    /***
     * Activityの「onResume」に基づき開始される
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mWebView.onResume();
    }

    /***
     * Activityが「onPause」になった場合や、Fragmentが変更更新されて操作を受け付けなくなった場合に呼び出される
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mWebView.onPause();
    }

    /***
     * フォアグラウンドでなくなった場合に呼び出される
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        mWebView.onPause();
    }

    /***
     * Activityが破棄される時、最後に呼び出される
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mWebView.destroy();
    }



}
