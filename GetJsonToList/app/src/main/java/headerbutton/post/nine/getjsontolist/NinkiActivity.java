package headerbutton.post.nine.getjsontolist;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

    // WebView
    private WebView mWebView;
    private static String top_url = "http://9post.jp/ranking";

    // ローディングダイアログ
    private Dialog waitDialog;
    private Integer sleep_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ninki);

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
            // 下層ページ
            else if(is_sub(url)) {

                // Volleyスタート
//                requestVolley(url);

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

                            // TODO YouTubeフラグメント起動 （v4の作法で）
                        }else{
                            // YouTubeじゃない場合
                            
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
        getMenuInflater().inflate(R.menu.menu_ninki, menu);
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
