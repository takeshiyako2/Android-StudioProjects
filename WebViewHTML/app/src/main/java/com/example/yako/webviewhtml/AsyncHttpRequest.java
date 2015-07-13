package com.example.yako.webviewhtml;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


import android.net.Uri;
import android.os.AsyncTask;
import android.app.Activity;
import android.widget.TextView;
import org.apache.http.*;
import org.apache.http.util.*;
import org.apache.http.impl.client.*;
import org.apache.http.client.methods.*;


import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;


public class AsyncHttpRequest extends AsyncTask<String, Void, String> {
    public Activity owner;
    private String ReceiveStr;


    //API key
    private static final String DEVELOPER_KEY = "AIzaSyBg9BcfszdOvxNglMZ5celamxJxexP5u30";

    //Youtube のビデオID
    private static String videoId = "EGy39OMyHzw";

    // リカバリー·リクエストの値を設定
    private static final int RECOVERY_DIALOG_REQUEST = 1;



    public AsyncHttpRequest(Activity activity) {
        // 呼び出し元のアクティビティ
        owner = activity;
    }

    // このメソッドは必ずオーバーライドする必要があるよ
    // ここが非同期で処理される部分みたいたぶん。
    @Override
    protected String doInBackground(String... url) {
        try {
            HttpGet httpGet = new HttpGet(url[0]);

            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpGet.setHeader("Connection", "Keep-Alive");

            HttpResponse response = httpClient.execute(httpGet);
            int status = response.getStatusLine().getStatusCode();
            if (status != HttpStatus.SC_OK) {
                throw new Exception("");
            } else {
                ReceiveStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // このメソッドは非同期処理の終わった後に呼び出されます
    @Override
    protected void onPostExecute(String result) {
        TextView textView2 = (TextView) owner.findViewById(R.id.textView);
        textView2.setText(ReceiveStr);

    }

}






