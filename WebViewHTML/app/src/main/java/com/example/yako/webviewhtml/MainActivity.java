package com.example.yako.webviewhtml;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;

public class MainActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {
//public class MainActivity extends ActionBarActivity   {

    // WebView
    private WebView mWebView;
    private static String top_url = "https://www.google.co.jp/";

    //API key
    private static final String DEVELOPER_KEY = "AIzaSyBg9BcfszdOvxNglMZ5celamxJxexP5u30";

    //Youtube のビデオID
    private static String videoId = "EGy39OMyHzw";

    // リカバリー·リクエストの値を設定
    private static final int RECOVERY_DIALOG_REQUEST = 1;


    // // プレーヤーを初期化する処理をまとめる
    private void initYouTubeView() {
        // インスタンスを取得
        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        // プレーヤーを初期化する
        youTubeView.initialize(DEVELOPER_KEY, this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // HttpClientの為にスレッド開始
        String url = "http://9post.jp/new-post";
        AsyncHttpRequest task = new AsyncHttpRequest(MainActivity.this);
        task.owner = MainActivity.this;
        task.execute(url);


        // AsyncHttpClient
//        AsyncHttpClient client = new AsyncHttpClient();


    }

    // ユーザーがリカバリー·アクションを実行した場合、再度プレーヤーを初期化
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            initYouTubeView();
        }
    }

    // プレーヤーの初期化失敗
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    // プレーヤーの初期化成功
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            // プレーヤーを再生
            player.loadVideo(videoId);
            // プレーヤーの設定 時間バーと再生/一時停止コントロールのみを表示
            player.setPlayerStyle(PlayerStyle.MINIMAL);
        }
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
