package com.example.yako.volleytest;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends ActionBarActivity implements YouTubePlayer.OnInitializedListener {


    private RequestQueue mQueue;

    //API key
    private static final String DEVELOPER_KEY = "AIzaSyAmG880XF_VyLirMrqCYroGIvfDTQMMZHQ";

    //Youtube のビデオID
    private static String videoId = "EGy39OMyHzw";

    // リカバリー·リクエストの値を設定
    private static final int RECOVERY_DIALOG_REQUEST = 1;


    private final static String TAG = "TEST";

    // プレーヤーを初期化する処理をまとめる
    private void initYouTubeView() {
        /*
        // インスタンスを取得
        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        // プレーヤーを初期化する
        youTubeView.initialize(DEVELOPER_KEY, this);
*/
        // フラグメントインスタンスを取得
        YouTubePlayerFragment youTubePlayerFragment =
                (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_view);

        // フラグメントのプレーヤーを初期化する
        youTubePlayerFragment.initialize(DEVELOPER_KEY, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Volleyスタート
        requestVolley();
    }

    private void requestVolley() {
        // Volley でリクエスト
        final TextView mTextView = (TextView) findViewById(R.id.text);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://9post.jp/20905";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        mTextView.setText("Response is: "+ response.substring(0,500));

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
