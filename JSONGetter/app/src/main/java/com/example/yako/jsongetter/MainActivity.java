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

    // JSONデータ取得URL
    private final String URL_API = "http://weather.livedoor.com/forecast/webservice/json/v1?city=130010";

    // HTTPリクエスト管理キュー
    private RequestQueue queue;

    //  Volleyでリクエスト時に設定するタグ名、キャンセル時に利用 クラス名をタグ指定
    private static final Object TAG_REQUEST_QUEUE = MainActivity.class.getName();

    // 地区名用テキストビュー
    TextView textview_title;

    // 地区名用テキストビュー
    TextView textview_description;

    // 予報表示用リストビューのアダプター
    ArrayAdapter<String> adapter;

    // ログ出力用のタグ
    private String TAG = "JSONGetter";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG, "onCreate");

        // ビューを設定
        // 地区名
        textview_title = (TextView) findViewById(R.id.textview_title);
        // 予報
        ListView listview_forecasts = (ListView) findViewById(R.id.listview_forecasts);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listview_forecasts.setAdapter(adapter);
        // 天気概況
        textview_description = (TextView) findViewById(R.id.textview_description);

        // VolleyでHTTPリクエスト管理キューを生成
        queue = Volley.newRequestQueue(this);
    }

    private void requestJsonObject(){

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Method.GET, URL_API, null,
            new Listener<JSONObject>() {
                // レスポンス受信のリスナー
                @Override
                public void onResponse(JSONObject response) {
                    // ログ出力
                    Log.d(TAG, "onResponse: " + response.toString());
                    try {
                        // 地区名を取得、テキストビューに登録
                        String title = response.getString("title");
                        textview_title.setText(title);

                        // 天気概況文を取得、テキストビューに登録
                        JSONObject description = response.getJSONObject("description");
                        String description_text = description.getString("text");
                        textview_description.setText(description_text);

                        // リストビューをクリア、publicで宣言しているので、そのままだと、バックグラウンドから戻った時にどんどん追加されてしまうため
                        adapter.clear();

                        // 天気予報の予報日毎の配列を取得
                        JSONArray forecasts = response.getJSONArray("forecasts");
                        for (int i = 0; i < forecasts.length(); i++) {
                            JSONObject forecast = forecasts.getJSONObject(i);
                            // 日付を取得
                            String date = forecast.getString("date");
                            // 予報を取得
                            String telop = forecast.getString("telop");
                            // リストビューに登録
                            adapter.add(date + ":" + telop);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            },
            new Response.ErrorListener() {
                // リクエストエラーのリスナー
                @Override
                public void onErrorResponse(VolleyError error) {
                    // エラー処理
                    Log.e(TAG, "onErrorResponse : " + error.getMessage());
                }

            }
        );

        // タグを設定する
        jsonRequest.setTag(TAG_REQUEST_QUEUE);

        // リクエスト＆レスポンス情報の設定をキューに追加
        queue.add(jsonRequest);

        // キュー処理をスタート、暗黙的にリクエストキューは発行されるが、見た目的にわかりやすくなるので書いた
        queue.start();

    }

    // onCreateの後に呼び出される
    @Override
    public void onStart(){
        super.onStart();
        Log.e(TAG, "onStart");
        requestJsonObject();
    }

    // onStopのときにキューをキャンセル
    @Override
    public void onStop(){
        super.onStop();
        Log.e(TAG, "onStop");
        queue.cancelAll(TAG_REQUEST_QUEUE);
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
