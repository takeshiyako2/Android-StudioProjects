package headerbutton.post.nine.getjsontolist;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TimePicker;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    // JSONデータ取得URL
    private String BASE_URL_API = "http://9post.top.json.hitokoto.co/new-post";
    private String URL_API;

    // Play URL (短縮URL)
    String play_url = "";

    //  Volleyでリクエスト時に設定するタグ名、キャンセル時に利用 クラス名をタグ指定
    private static final Object TAG_REQUEST_QUEUE = MainActivity.class.getName();

    // ログ出力用のタグ
    private static final String TAG = MainActivity.class.getSimpleName();

    // Volleyへ渡すタグ
    String tag_json_obj = "json_obj_req";

    // フッターのクルクル
    View mFooter;

    // リストビュー
    ListView listview;

    // リストビューのアイテム
    RowDetail item;

    // 予報表示用リストビューのアダプター
    RowDetailAdapter rowAdapater;

    // いまはFlagmentかどうか
    private Boolean ThisIsFlagment = false;

    // リフレッシュ
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // 通知
    TimePicker tPicker;
    int notificationId = 100;
    private PendingIntent alarmIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // アイコン設定
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(R.drawable.nine_post_icon_small);

        // リストビューへ紐付け
        listview = (ListView) findViewById(R.id.listview_forecasts);
        listview.addFooterView(getFooter()); // クルクル

        // 表示用のリストを用意
        List<RowDetail> objects = new ArrayList<RowDetail>();

        // ArrayAdapterへ設定
        rowAdapater = new RowDetailAdapter(this, 0, objects);

        // リストビューへリストを登録
        listview.setAdapter(rowAdapater);

        // リストビューがクリックされたときのコールバックリスナーを登録
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // リストのアイテムをクリックした時の挙動
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.e(TAG, "onItemClick");

                // リストビューの項目を取得
                ListView listview = (ListView) parent;
                item = (RowDetail) listview.getItemAtPosition(position);

                // 下層ページ
                // YouTubeの場合
                int youtube_flag = item.getYouTubeFlag();
                if (youtube_flag == 1) {

                    // MainActivity LinearLayout非表示
                    findViewById(R.id.layout_list).setVisibility(View.GONE);

                    // Fragmentに受け渡す値
                    Bundle args = new Bundle();
                    args.putString("id", item.getId());
                    args.putString("title", item.getTitle());
                    args.putString("url", item.getUrl());
                    args.putString("youtube_id", item.getYouTubeID());

                    // フラグをtrue
                    ThisIsFlagment = true;

                    // アクションバーに戻る(<-)を表示
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                    // YouTubeフラグメント起動 （v4の作法で）
                    SubFragment fragment = new SubFragment();
                    fragment.setArguments(args);
                    FragmentManager manager = getSupportFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.framelayout1, fragment)
                            .commit();
                }
                // その他の場合
                else {
                    // Intentを起動(ブラウザを開く)
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(item.getUrl())));
                }
            }
        });

        // スクロール処理
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mark = 0;
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 最初とスクロール完了したとき
                if ((totalItemCount - visibleItemCount) == firstVisibleItem && totalItemCount > mark) {
                    mark = totalItemCount;
                    Integer page = ((mark - 1) / 10) + 1;

                    if (page >= 2) {
                        URL_API = BASE_URL_API + "/page/" + page + "?index=top";
                    } else {
                        URL_API = BASE_URL_API + "?index=top";
                    }
                    // リクエスト処理
                    request();
                }
            }
            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
            }
        });

        // スワイプで更新
        createSwipeRefreshLayout();

        // アラームの時間設定
        int hour = 12;
        int minute = 00;
        long alarmStartTime = get_time_by_hour_minuite(hour, minute);
        Log.d(TAG, "IntentService" + " " + String.valueOf(alarmStartTime));

        // アラームセット
        Intent bootIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        bootIntent.putExtra("notificationId", notificationId);
        alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 102, bootIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        // リピート
        alarm.setRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmStartTime,
//                1 * 1000 * 30, // 30秒毎
                AlarmManager.INTERVAL_DAY, // 1日毎
                alarmIntent
        );

    }

    // 次のアラームの時刻を取得
    public long get_time_by_hour_minuite(int hour, int minuite) {
        // 日本(+9)以外のタイムゾーンを使う時はここを変える
        TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
        //今日の目標時刻のカレンダーインスタンス作成
        Calendar cal_target = Calendar.getInstance();
        cal_target.setTimeZone(tz);
        cal_target.set(Calendar.HOUR_OF_DAY, hour);
        cal_target.set(Calendar.MINUTE, minuite);
        cal_target.set(Calendar.SECOND, 0);
        //現在時刻のカレンダーインスタンス作成
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTimeZone(tz);
        //ミリ秒取得
        long target_ms = cal_target.getTimeInMillis();
        long now_ms = cal_now.getTimeInMillis();
        //今日ならそのまま指定
        if (target_ms >= now_ms) {
            //過ぎていたら明日の同時刻を指定
        } else {
            cal_target.add(Calendar.DAY_OF_MONTH, 1);
            target_ms = cal_target.getTimeInMillis();
        }
        return target_ms;
    }

    // スワイプのレイアウト
    public void createSwipeRefreshLayout(){
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_dark, android.R.color.holo_red_light, android.R.color.holo_orange_dark, android.R.color.holo_orange_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    // スワイプ引っ張ったタイミング
    @Override
    public void onRefresh() {
        // アクティビティ終了
        finish();
        // アクティビティ開始
        startActivity(new Intent(this, MainActivity.class));
        // アクティビティ移行時のアニメーションを無効化
        overridePendingTransition(0, 0);
    }

    // フッターのクルクル
    private View getFooter() {
        if (mFooter == null) {
            mFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);
        }
        return mFooter;
    }

    // volleyのリクエスト処理を作成
    private void request() {
        Log.d(TAG, "request URL_API: " + URL_API);
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, URL_API, null,
                new Response.Listener<JSONArray>() {
                    // レスポンス受信のリスナー
                    @Override
                    public void onResponse(JSONArray response) {
                        // ログ出力
                        Log.d(TAG, "request onResponse: " + response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject forecast = response.getJSONObject(i);

                                // JSONから取得
                                String id = forecast.getString("id");
                                String title = forecast.getString("title");
                                String url = forecast.getString("url");
                                String image = forecast.getString("image");
                                String youtube_id = forecast.getString("youtube_id");
                                int youtube_flag = forecast.getInt("youtube_flag");

                                // リストにアイテムを追加
                                RowDetail item = new RowDetail();
                                item.setId(id);
                                item.setTitle(title);
                                item.setUrl(url);
                                item.setImage(image);
                                item.setYouTubeID(youtube_id);
                                item.setYouTubeFlag(youtube_flag);

                                // ArrayAdapterへ設定
                                rowAdapater.add(item);
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
                        Log.d(TAG, "request Error: " + error.getMessage());
                        if( error instanceof NetworkError) {
                        } else if( error instanceof ServerError) {
                        } else if( error instanceof AuthFailureError) {
                        } else if( error instanceof ParseError) {
                        } else if( error instanceof NoConnectionError) {
                        } else if( error instanceof TimeoutError) {
                        }
                    }

                }
        );

        // シングルトンクラスで、リクエストをvolleyのキューに入れる
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
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
                // MainActivity終了
                finish();
                // MainActivityアクティビティ開始
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
            // 設定
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
        int backStackCnt = getSupportFragmentManager().getBackStackEntryCount();

        // Fragmentから戻る処理
        if (ThisIsFlagment) {

            // MainActivity LinearLayout表示
            findViewById(R.id.layout_list).setVisibility(View.VISIBLE);

            // アクションバーの戻る(<-)を消す
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            // フラグをfalse
            ThisIsFlagment = false;

            // Fragment終了
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(getSupportFragmentManager().findFragmentById(R.id.framelayout1))
                    .commit();
        }
        // アクティビティ終了
        else{
            finish();
        }

    }

}
