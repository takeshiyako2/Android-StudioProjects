package com.example.yako.intenttest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.view.KeyEvent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.graphics.Bitmap;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class MainActivity extends Activity {

    private Common common;
    private int count1;
    private int count2;

    /** エラーページ */
    private View mErrorPage;

    /** ページ取得失敗判定 */
    private boolean mIsFailure = false;

    static final int MY_INTENT_BROWSER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        // グローバル変数を扱うクラスを取得する
        common = (Common) getApplication();
        // グローバル変数を扱うクラスを初期化する
        common.init();

        // エラーページのレイアウト
        mErrorPage= findViewById(R.id.webview_error_page);

        //レイアウトで指定したWebViewのIDを指定する。
        WebView myWebView = (WebView)findViewById(R.id.webview);

        // WebViewClientの設定
        myWebView.setWebViewClient(new CustomWebViewClient());
        WebSettings webSettings = myWebView.getSettings();

        // ユーザーエージェントの設定
        String ua = myWebView.getSettings().getUserAgentString();
//        Log.d("user-agent", ua);
        ua = ua + " YourUserAgent";
        myWebView.getSettings().setUserAgentString(ua);

        // ViewportのサイズをWebView側にも反映させる
        myWebView.getSettings().setUseWideViewPort(true);

        // JavaScript の使用
        webSettings.setJavaScriptEnabled(true);
        myWebView.getSettings().setJavaScriptEnabled(true);

        // WebViewのキャッシュ処理
        myWebView.getSettings().setAppCacheEnabled(true);
        myWebView.getSettings().setAppCacheMaxSize(8 * 1024 * 1024);
        myWebView.getSettings().setAppCachePath("/mywebview/cache/");

        //最初にYahoo! Japanのページを表示する。
        myWebView.loadUrl(common.ResourceUrl);


    }

    @OnClick(R.id.button_back)
    void clickButtonBack() {
        count1++;
        TextView textView = (TextView) findViewById(R.id.text_content);
        textView.setText("BACK:" + count1);


/*
        // ShareCompatを使って簡単にシェアボタンを実装
        String articleURL = "記事のURL";
        String articleTitle = "記事のタイトル";
        String sharedText = articleTitle + " " + articleURL;

        // builderの生成　ShareCompat.IntentBuilder.from(Context context);
        ShareCompat.IntentBuilder builder = ShareCompat .IntentBuilder.from(MainActivity.this);

        // シェアするタイトル
        builder.setSubject(articleTitle);

        // シェアするテキスト
        builder.setText(sharedText);

        // シェアするタイプ（他にもいっぱいあるよ）
        builder.setType("text/plain");

        // Shareアプリ一覧のDialogの表示
        builder.startChooser();
*/

        // シェア FB -> URLのみシェアできる
        share("com.facebook.katana", "http://qiita.com/ueno-yuhei/items/f5c7b36e2931a9da143f");

    }

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
            Toast.makeText(MainActivity.this,
                    "not installed fb",
                    Toast.LENGTH_LONG).show();
        }

    }

    @OnClick(R.id.button_next)
    void clickButtonNext() {
        count2++;
        TextView textView = (TextView) findViewById(R.id.text_content);
        textView.setText("NEXT " + count2);

        // URLスキームでシェア TW
        Intent intent = null;
        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://post?message=test"));
        try {
            startActivityForResult(intent, MY_INTENT_BROWSER);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this,
                    "not installed twitter",
                    Toast.LENGTH_LONG).show();
        }

    }

    // シェア返り値
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //返り値の取得
        if (resultCode == RESULT_OK) {
            // Success
            Log.d("IntentSample", "success");
        } else if (resultCode == RESULT_CANCELED) {
            // Handle cancel
            Log.d("IntentSample", "canceled");
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView myWebView = (WebView)findViewById(R.id.webview);
        // 端末のBACKキーで一つ前のページヘ戻る
        if(keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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


    // ページ読み込み時の処理
    private class CustomWebViewClient extends WebViewClient{
        private ProgressDialog waitDialog;

        public CustomWebViewClient() {
            super();
        }

        // エラーだったらフラグをtrueにします。
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            mIsFailure = true;
        }

        // ページ読み込み開始
        @Override
        public void onPageStarted (WebView view, String url, Bitmap favicon){

            // グローバル変数を扱うクラスを取得する
            common = (Common) getApplication();
            // グローバル変数を扱うクラスを初期化する
            common.init();

            String regex = common.ResourceUrl;
            Pattern p = Pattern.compile(regex);
            Matcher m1 = p.matcher(url);

            //Loading....
            if (waitDialog == null && m1.find()) {
                waitDialog = new ProgressDialog(view.getContext());
                waitDialog.setMessage("読み込み中...." + url);
                waitDialog.setProgressStyle(
                        ProgressDialog.STYLE_SPINNER);
                waitDialog.show();
            }

        }
        // ページ読み込み完了
        @Override
        public void onPageFinished (WebView view, String url){

            // 読み込みエラー処理
            if (mIsFailure) {
                // エラー表示
                mErrorPage.setVisibility(View.VISIBLE);
            } else {
                // エラー非表示
                mErrorPage.setVisibility(View.GONE);
            }

            waitDialog.dismiss();
            waitDialog = null;
        }
    }
}
