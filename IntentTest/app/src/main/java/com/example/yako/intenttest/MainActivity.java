package com.example.yako.intenttest;

import android.app.Activity;
import android.os.Bundle;
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
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class MainActivity extends Activity {

    private Common common;
    private int count1;
    private int count2;

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

        //レイアウトで指定したWebViewのIDを指定する。
        WebView myWebView = (WebView)findViewById(R.id.webview);
        // WebViewClientの設定
        myWebView.setWebViewClient(new CustomWebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        // ユーザーエージェントの設定
//        myWebView.getSettings().setUserAgentString("YourUserAgent");
        // ViewportのサイズをWebView側にも反映させる
        myWebView.getSettings().setUseWideViewPort(true);
        // JavaScript の使用
        webSettings.setJavaScriptEnabled(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        //最初にYahoo! Japanのページを表示する。
        myWebView.loadUrl(common.ResourceUrl);


    }

    @OnClick(R.id.button_back)
    void clickButtonBack() {
        count1++;
        TextView textView = (TextView) findViewById(R.id.text_content);
        textView.setText("BACK:" + count1);
    }

    @OnClick(R.id.button_next)
    void clickButtonNext() {
        count2++;
        TextView textView = (TextView) findViewById(R.id.text_content);
        textView.setText("NEXT:" + count2);
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
            waitDialog.dismiss();
            waitDialog = null;
        }
    }
}
