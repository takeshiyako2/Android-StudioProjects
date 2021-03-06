package com.example.yako.headerbutton;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

import android.os.Handler;

public class MainActivity extends ActionBarActivity implements YouTubePlayer.OnInitializedListener {

    private static String TAG = "MainActivity";

    // WebView
    private WebView mWebView;
    private static String top_url = "http://9post.jp/";
    private static String menu1_url = "http://9post.jp/";
    private static String menu2_url = "http://9post.jp/ranking";

    // エラーページ
    private View mErrorPage;

    // ページ取得失敗判定
    private boolean mIsFailure = false;

    // シェアボタン
    private Button button1;
    private Button button2;

    // 通知
    TimePicker tPicker;
    int notificationId = 100;
    private PendingIntent alarmIntent;

    // YouTube
    private YouTubePlayerFragment youTubePlayerFragment;
    private static final String DEVELOPER_KEY = "AIzaSyAmG880XF_VyLirMrqCYroGIvfDTQMMZHQ";
    private static String videoId = "EGy39OMyHzw";
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    // ローディングダイアログ
    private Dialog waitDialog;
    private Integer sleep_time;

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
        mWebView.setWebViewClient(new MyWebViewClient(this));
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        String ua = mWebView.getSettings().getUserAgentString();
        ua = ua + " 9post-android";
        mWebView.getSettings().setUserAgentString(ua);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mErrorPage= findViewById(R.id.webview_error_page);
        mWebView.setBackgroundColor(0);
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
        int minute = 00;
        long alarmStartTime = get_time_by_hour_minuite(hour, minute);
        Log.d(TAG, "IntentService" + " " + String.valueOf(alarmStartTime));

        // アラームセット
        Intent bootIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        bootIntent.putExtra("notificationId", notificationId);
        alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 102, bootIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        // リピート
        alarm.setRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmStartTime,
                AlarmManager.INTERVAL_DAY, // 1日毎
                alarmIntent
        );
//        notificationId++; // 何回も同じ通知をしないように。notificationIdは固定にする。
    }

    // WebViewClientを継承
    public class MyWebViewClient extends WebViewClient {

        // ローディングダイアログ
        public MyWebViewClient(Context c) {
            waitDialog = new Dialog(c, R.style.Theme_CustomProgressDialog);
            waitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            waitDialog.setContentView(R.layout.custom_progress_dialog);
            waitDialog.getWindow().setFlags(0, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        // エラーが発生した場合
        @Override
        public void onReceivedError(WebView webview, int errorCode, String description, String failingUrl) {
            mIsFailure = true;
        }

        // ページの読み込み前に呼ばれる
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            // トップページ
            if (is_top(url)) {

                // 非表示
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.GONE);

                // 表示
                findViewById(R.id.linearLayout_webview).setVisibility(View.VISIBLE);

                // スレッドのローディングダイアログをスタート
                sleep_time = 1300;
                try{
                    waitDialog.show();
                    // 実際に行いたい処理は、プログレスダイアログの裏側で行うため、別スレッドにて実行する
                    (new Thread(runnable)).start();
                }catch(Exception ex){
                }finally{
                }

                // アクションバーの戻る(<-)を消す
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            // 下層ページ
            } else if(is_sub(url)) {

                // Volleyスタート
                requestVolley(url);

                // 非表示
                findViewById(R.id.linearLayout_webview).setVisibility(View.GONE);

                // 表示
                button1.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                findViewById(R.id.linearLayout_youtube).setVisibility(View.VISIBLE);

                // JavaScriptが重いのでオフ
                WebSettings webSettings = mWebView.getSettings();
                webSettings.setJavaScriptEnabled(false);

                // ローディングダイアログの表示位置　下部に表示
//                WindowManager.LayoutParams wmlp=waitDialog.getWindow().getAttributes();
//                wmlp.gravity = Gravity.BOTTOM;
//                wmlp.y = 450;
//                waitDialog.getWindow().setAttributes(wmlp);

                // アクションバーに戻る(<-)を表示
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // その他のページ
            } else {

                // 表示
                findViewById(R.id.linearLayout_youtube).setVisibility(View.GONE);
                findViewById(R.id.linearLayout_webview).setVisibility(View.VISIBLE);

                // アクションバーに戻る(<-)を表示
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

            // 下層ページ
            if(is_sub(url)) {
                // 読み込み完了時にwebviewを表示
                findViewById(R.id.linearLayout_webview).setVisibility(View.VISIBLE);
            }
        }
    }

    // トップページ判定
    public boolean is_top(String url) {
        if (url.equals(top_url)) {
            return true;
        }else{
            return false;
        }
    }

    // 下層ページ判定
    public boolean is_sub(String url) {
        String str = url;
        Pattern p = Pattern.compile("http://9post.jp/[0-9]*$");
        Matcher a = p.matcher(str);
        if (a.find()) {
            return true;
        }else{
            return false;
        }
    }

    // スレッドのローディングダイアログを終了
    private Runnable runnable = new Runnable(){
        public void run() {
            // ここではダミーでスリープを行う
            // 実際にはここに処理を書く
            try {
                Thread.sleep(sleep_time);
            } catch (InterruptedException e) {
                Log.e("Runnable", "InterruptedException");
            }
            // 処理が完了したら、ローディングダイアログを消すためにdismiss()を実行する
            waitDialog.dismiss();
        }
    };

    // Volley でリクエスト
    private void requestVolley(String url) {
        // エラー用
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
                        }else{
                            // YouTubeじゃない場合
                            mErrorPage.setVisibility(View.VISIBLE);
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

    // YouTubeプレーヤーを初期化する処理をまとめる
    private void initYouTubeView() {
        Log.d(TAG, "initYouTubeView");
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
            // プレーヤーの設定 すべてのインタラクティブなコントロールを表示
            player.setPlayerStyle(PlayerStyle.DEFAULT);
            player.setShowFullscreenButton(false);
            player.play();
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
            // 新着
            case R.id.menu1:
                top_url = menu1_url;
                // アクティビティ開始
                startActivity(new Intent(this, MainActivity.class));
                // アクティビティ移行時のアニメーションを無効化
                overridePendingTransition(0, 0);
                return true;
            // 人気
            case R.id.menu2:
                top_url = menu2_url;
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                return true;
            // 設定
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            // 下層ページで、戻る（<-）を押したときの処理
            case android.R.id.home:
                makeBack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 戻るボタン
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        makeBack();
    }

    // 下層ページから戻る処理
    public void makeBack() {

        String url = mWebView.getUrl();

        // Top層の場合
        if (url.equals(top_url)){
            // アプリ終了
            finish();
        }
        // 下層の場合　ティビティを起動
        else{
            // アクティビティスタックを破棄
            finish();
            // トップ
            if (top_url.equals(menu1_url)){
                top_url = menu1_url;
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
            }
            // ランキング
            if (top_url.equals(menu2_url)){
                top_url = menu2_url;
                startActivity(new Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
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
