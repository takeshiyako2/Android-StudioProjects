package headerbutton.post.nine.getjsontolist;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NinkiActivity extends ActionBarActivity {

    private static String TAG = "NinkiActivity";

    // いまはFlagmentかどうか
    private Boolean ThisIsFlagment = false;

    // WebView
    private WebView mWebView;
    private static String top_url = "http://9post.jp/ranking";

    // ローディングダイアログ
    private Dialog waitDialog;
    private Integer sleep_time;

    // リクエストするURL
    String request_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ninki);

        // アイコン設定
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(R.drawable.nine_post_icon_small);

        // WebViewの設定
        mWebView = (WebView) findViewById(R.id.webView1);
        mWebView.setWebViewClient(new MyWebViewClient(this));
        WebSettings webSettings = mWebView.getSettings();
//        webSettings.setJavaScriptEnabled(true);
        String ua = mWebView.getSettings().getUserAgentString();
        ua = ua + " 9post-android";
        mWebView.getSettings().setUserAgentString(ua);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.clearCache(true);
        mWebView.clearHistory();
        mWebView.setBackgroundColor(0);
        mWebView.loadUrl(top_url);
    }

    // WebViewClientを継承
    public class MyWebViewClient extends WebViewClient {

        // 新しいURLが指定されたときの処理を定義
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.e(TAG, "url:" + url);
            // 下層ページ
            if(is_sub(url)) {

                // アクションバーに戻る(<-)を表示
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                // webView1非表示
                findViewById(R.id.webView1).setVisibility(View.GONE);

                // Volleyスタート(中でFlagmentを起動)
                requestVolley(url);

                // WebView内に読み込み結果を表示しない　別のActivityやアプリを起動する場合
                return true;
            }
            return false;
        }

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
        }

        // ページの読み込み前に呼ばれる
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // トップページ
            if (is_top(url)) {
                // スレッドのローディングダイアログをスタート
                sleep_time = 1300;
                try {
                    waitDialog.show();
                    // 実際に行いたい処理は、プログレスダイアログの裏側で行うため、別スレッドにて実行する
                    (new Thread(runnable)).start();
                } catch (Exception ex) {
                } finally {
                }
                // アクションバーの戻る(<-)を消す
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }

        // ページ読み込み完了時に呼ばれる
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
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

        request_url = url;

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
                            String videoId = matchstr;
                            Log.d(TAG, videoId);

                            // Fragmentに受け渡す値
                            Bundle args = new Bundle();
                            args.putString("id", "");
                            args.putString("title", "");
                            args.putString("url", request_url);
                            args.putString("youtube_id", videoId);

                            // YouTubeフラグメント起動 （v4の作法で）
                            SubFragment fragment = new SubFragment();
                            fragment.setArguments(args);
                            FragmentManager manager = getSupportFragmentManager();
                            manager.beginTransaction()
                                    .replace(R.id.framelayout1, fragment)
                                    .commit();

                            // フラグをtrue
                            ThisIsFlagment = true;
                        }else{
                            // YouTubeじゃない場合
                            // Intentを起動(ブラウザを開く)
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(request_url)));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // アクションバーのアクションを受け取る
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // 新着
            case R.id.menu1:
                // MinkiActivity終了
                finish();
                // アクティビティ開始
                startActivity(new Intent(this, MainActivity.class));
                // アクティビティ移行時のアニメーションを無効化
                overridePendingTransition(0, 0);
                return true;
            // 人気
            case R.id.menu2:
                finish();
                startActivity(new Intent(this, NinkiActivity.class));
                overridePendingTransition(0, 0);
                return true;
            // Settingsを押したときの処理
            case R.id.action_settings:
                finish();
                startActivity(new Intent(this, SettingsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            // Facebookページ
            case R.id.action_facebook_page:
                OpenFacebookPage openFacebookPage = new OpenFacebookPage(this);
                openFacebookPage.makeIntent();
                return true;
            // 戻る（<-）を押したときの処理
            case android.R.id.home:
                makeBackFromFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 戻るボタンを押した時
    @Override
    public void onBackPressed() {
        makeBackFromFragment();
    }

    // 戻る処理
    public void makeBackFromFragment() {

        // Fragmentから戻る処理
        if (ThisIsFlagment) {

            // Fragment終了
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(getSupportFragmentManager().findFragmentById(R.id.framelayout1))
                    .commit();

            // webView1表示
            findViewById(R.id.webView1).setVisibility(View.VISIBLE);

            // フラグをfalse
            ThisIsFlagment = false;

            // アクションバーの戻る(<-)を消す
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        // アクティビティ終了
        else{
            finish();
            // MainActivity開始
            startActivity(new Intent(this, MainActivity.class));
            // アクティビティ移行時のアニメーションを無効化
            overridePendingTransition(0, 0);
        }
    }
}
