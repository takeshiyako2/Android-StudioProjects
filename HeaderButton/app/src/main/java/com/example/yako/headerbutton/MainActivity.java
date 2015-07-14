package com.example.yako.headerbutton;

import android.app.AlarmManager;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;
import java.util.TimeZone;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import android.widget.Toast;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity implements YouTubePlayer.OnInitializedListener {

    private static String TAG = "MainActivity";

    // WebView
    private WebView mWebView;
    private static String top_url = "http://9post.jp/";
    private static String menu1_url = "http://9post.jp/";
    private static String menu2_url = "http://9post.jp/ranking";
//    private static String top_url = "https://www.google.co.jp/";
//    private static String menu1_url = "https://www.google.co.jp/";
//    private static String menu2_url = "https://www.facebook.com/";

    // エラーページ
    private View mErrorPage;

    // ページ取得失敗判定
    private boolean mIsFailure = false;

    // シェアボタン
    private Button button1;
    private Button button2;

    // 通知
    TimePicker tPicker;
    int notificationId;
    private PendingIntent alarmIntent;

    // YouTube
    private YouTubePlayerFragment youTubePlayerFragment;
    private static final String DEVELOPER_KEY = "AIzaSyAmG880XF_VyLirMrqCYroGIvfDTQMMZHQ";
    private static String videoId = "EGy39OMyHzw";
    private static final int RECOVERY_DIALOG_REQUEST = 1;

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

        // アイコン設定
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(R.drawable.nine_post_icon_small);

        // WebViewの設定
        mWebView = (WebView) findViewById(R.id.webView1);
        mWebView.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String ua = mWebView.getSettings().getUserAgentString();
        ua = ua + " 9post-android";
        mWebView.getSettings().setUserAgentString(ua);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mErrorPage= findViewById(R.id.webview_error_page);
        mWebView.loadUrl(top_url);

        // シェアボタン
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        // シェアボタンのリスナー設定
        button1.setOnClickListener(new ButtonAction());
        button2.setOnClickListener(new ButtonAction());
        // シェアボタン最初は非表示 TOPで表示/下層で非表示
        button1.setVisibility(View.GONE);
        button2.setVisibility(View.GONE);

        // アラームの時間設定
        int hour = 12;
        int minute = 0;
        long alarmStartTime = get_time_by_hour_minuite(hour, minute);
        Log.d(TAG, "IntentService" + " " + String.valueOf(alarmStartTime));

        // アラームの時間設定 デバッグ用（本番時にはコメントアウトしておく）
        /*
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, hour);
        startTime.set(Calendar.MINUTE, minute);
        startTime.set(Calendar.SECOND, 0);
        alarmStartTime = startTime.getTimeInMillis();
        */

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
        notificationId++;
    }

    private void requestVolley(String url) {
        // Volley でリクエスト
        final TextView mTextView = (TextView) findViewById(R.id.volley_error_page);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // YouTube ID切り出し
                        String str = response;
                        String regex = "https://www.youtube.com/embed/(.*)\\?";
                        Pattern p = Pattern.compile(regex);
                        Matcher m = p.matcher(str);
                        if (m.find()){
                            String matchstr = m.group(1);
                            videoId = matchstr;
                            Log.d(TAG, videoId);

                            // YouTube初期化
                            initYouTubeView();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mTextView.setText("That didn't work!" + error.getMessage());
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    // WebViewClientを継承
    public class MyWebViewClient extends WebViewClient {

        // エラーが発生した場合
        @Override
        public void onReceivedError(WebView webview, int errorCode, String description, String failingUrl) {
            mIsFailure = true;
        }

        // ページの読み込み前に呼ばれる
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.equals(top_url)) {
                // トップ
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);
            }else {
                // /下層
                button1.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                findViewById(R.id.linearLayout_youtube).setVisibility(View.VISIBLE);
                // Volleyスタート
                requestVolley(url);
            }
        }
        // ページ読み込み完了時に呼ばれる
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // エラー表示
            if (mIsFailure) {
                mErrorPage.setVisibility(View.VISIBLE);
            } else {
                mErrorPage.setVisibility(View.GONE);
            }
        }
    }

    // YouTubeプレーヤーを初期化する処理をまとめる
    private void initYouTubeView() {
        // フラグメントインスタンスを取得
        YouTubePlayerFragment youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_view);
        // フラグメントのプレーヤーを初期化する
        youTubePlayerFragment.initialize(DEVELOPER_KEY, this);
    }

    // ユーザーがリカバリー·アクションを実行した場合、再度YouTubeプレーヤーを初期化
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            initYouTubeView();
        }
    }

    // YouTubeプレーヤーの初期化失敗
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    // YouTubeプレーヤーの初期化成功
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            // プレーヤーを再生
            player.loadVideo(videoId);
            // プレーヤーの設定 時間バーと再生/一時停止コントロールのみを表示
            player.setPlayerStyle(PlayerStyle.MINIMAL);
        }
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

    // シェアボタンのリスナー
    public class ButtonAction implements View.OnClickListener {

        // シェアボタンを押したとき
        @Override
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

    // メニュー作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // アクションバーを押したとき
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

    // 戻るボタン
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        finish(); // アクティビティスタックを破棄
        String url = mWebView.getUrl();
        if (url.equals(top_url)){
            // Top層の場合 そのままfinish
        }else{
            // 下層の場合 finishしてstartActivity
            if (top_url.equals(menu1_url)){
                top_url = menu1_url;
                startActivity(new Intent(this, MainActivity.class));
            }
            if (top_url.equals(menu2_url)){
                top_url = menu2_url;
                startActivity(new Intent(this, MainActivity.class));
            }
        }
    }

    // Activityの「onResume」に基づき開始される
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mWebView.onResume();
    }

    // Activityが「onPause」になった場合や、Fragmentが変更更新されて操作を受け付けなくなった場合に呼び出される
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        mWebView.onPause();
    }

    // フォアグラウンドでなくなった場合に呼び出される
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        mWebView.onPause();
    }

    // Activityが破棄される時、最後に呼び出される
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mWebView.destroy();
    }

}
