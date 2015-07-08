package com.example.yako.headerbutton;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private static String TAG = "MainActivity";
    private WebView mWebView;
    private static String top_url = "http://9post.jp/";
//    private static String top_url = "https://www.google.co.jp/";
    private Button button1;
    private Button button2;

    /** エラーページ */
    private View mErrorPage;

    /** ページ取得失敗判定 */
    private boolean mIsFailure = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // アイコン
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(R.drawable.nine_post_icon_small);

        // WebView
        mWebView = (WebView) findViewById(R.id.webView1);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // ユーザーエージェントの設定
        String ua = mWebView.getSettings().getUserAgentString();
        ua = ua + " 9post-android";
        mWebView.getSettings().setUserAgentString(ua);
        mWebView.loadUrl(top_url);

        // シェアボタンのリスナー設定
        button1 = (Button)findViewById(R.id.button1);
        button1.setOnClickListener(this);
        button2 = (Button)findViewById(R.id.button2);
        button2.setOnClickListener(this);

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

        switch (item.getItemId()) {

            case R.id.menu1:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.menu2:
                startActivity(new Intent(this, NinkiActivity.class));
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
        String url = mWebView.getUrl();
        if (url.equals(top_url)){
            // アクティビティを閉じる
            finish();
        }else{
            // WebViewで戻る
            mWebView.goBack();
        }
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
