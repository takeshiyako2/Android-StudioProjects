package com.example.yako.jsongetter;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


public class MainActivity extends ActionBarActivity {

    /** JSONデータ取得URL */
    private final String URL_API = "http://weather.livedoor.com/forecast/webservice/json/v1?city=130010";

    /** HTTPリクエスト管理Queue */
    private RequestQueue mQueue;

    /** 地区名用テキストビュー */
    TextView txtArea;

    /** 地区名用テキストビュー */
    TextView txtAreaDescription;

    /** 予報表示用リストビューのアダプター */
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 地区名用のテキストビュー
        txtArea = (TextView) findViewById(R.id.txtArea);
        // 天気概況文用のテキストビュー
        txtAreaDescription = (TextView) findViewById(R.id.txtAreaDescription);
        // 予報表示用のリストビュー
        ListView listForecast = (ListView) findViewById(R.id.listForecast);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listForecast.setAdapter(adapter);

        // HTTPリクエスト管理Queueを生成
        mQueue = Volley.newRequestQueue(this);

        // リクエスト実行
        mQueue.add(new JsonObjectRequest(Method.GET, URL_API, null, new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // --------------------------------------------
                // JSONObjectのパース、List、Viewへの追加等
                // --------------------------------------------
                // ログ出力
                Log.d("temakishiki", "response : " + response.toString());

                try {
                    // 地区名を取得し、設定
                    String title = response.getString("title");
                    txtArea.setText(title);

                    // 天気概況文を取得し、設定
                    JSONObject description = response.getJSONObject("description");
                    String description_text = description.getString("text");
                    txtAreaDescription.setText(description_text);

                    // 予報情報の一覧を取得
                    JSONArray forecasts = response.getJSONArray("forecasts");
                    for (int i = 0; i < forecasts.length(); i++) {
                        // 予報情報を取得
                        JSONObject forecast = forecasts.getJSONObject(i);
                        // 日付
                        String date = forecast.getString("date");
                        // 予報
                        String telop = forecast.getString("telop");

                        // リストビューに設定
                        adapter.add(date + ":" + telop);
                    }
                } catch (JSONException e) {
                    Log.e("temakishiki", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // --------------------------------------------
                // エラー処理 error.networkResponseで確認
                // --------------------------------------------
                if (error.networkResponse != null) {
                    Log.e("temakishiki", "エラー : " + error.networkResponse.toString());
                }
            }
        }));
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
