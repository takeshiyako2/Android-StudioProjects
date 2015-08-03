package news.splatoon.com.splatoon;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
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
import java.util.List;

public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    // JSONデータ取得URL
    private String BASE_URL_API = "http://api.splatoooon.hitokoto.co/feed.json";
    private String URL_API;

    // アプリタイトル
    String app_titile = "ニュース for スプラトゥーン";

    // Play URL (短縮URL)
    String play_url = "https://play.google.com/store/apps/details?id=news.splatoon.com.splatoon";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "onCreate");

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

                // MainActivity LinearLayout非表示
                findViewById(R.id.layout_list).setVisibility(View.GONE);

                // Fragmentに受け渡す値
                Bundle args = new Bundle();
                args.putString("url", item.getUrl());
                args.putInt("site_js_flag", item.getSiteJsFlag());

                // Fragment表示
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                WebviewFragment frag1 = new WebviewFragment();
                frag1.setArguments(args);
                fragmentTransaction.replace(R.id.framelayout1, frag1);
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.commit();

                // フラグをtrue
                ThisIsFlagment = true;

                // アクションバーに戻る(<-)を表示
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                // アクションバーに選んだタイトルを表示
                getSupportActionBar().setTitle(item.getTitle());
                getSupportActionBar().setSubtitle(item.getSiteTitle());
            }
        });

        /*
        // リストビューの項目が長押しされたときのコールバックリスナーを登録
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // リストビューの項目を取得
                ListView listview = (ListView) parent;
                RowDetail item = (RowDetail) listview.getItemAtPosition(position);
                String text = "長押ししました:" + item.getTitle() + ":" + item.getUrl();
                // トースト表示
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                // onItemClickを実行しない
                return true;
            }
        });
*/

        // スクロール処理
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mark = 0;

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                // 最初とスクロール完了したとき
                if ((totalItemCount - visibleItemCount) == firstVisibleItem && totalItemCount > mark) {
                    mark = totalItemCount;
                    Integer page = ((mark - 1) / 30) + 1;
                    URL_API = BASE_URL_API + "?p=" + page;

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
    }

    // スワイプのレイアウト
    public void createSwipeRefreshLayout(){
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark, android.R.color.holo_blue_light, android.R.color.holo_green_light, android.R.color.holo_green_dark);
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

    // リクエスト処理
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

                                // リストにアイテムを追加
                                RowDetail item = new RowDetail();
                                item.setId(forecast.getString("id"));
                                item.setTitle(forecast.getString("title"));
                                item.setSiteTitle(forecast.getString("site_title"));
                                item.setImage(forecast.getString("image"));
                                item.setSiteJsFlag(forecast.getInt("site_js_flag"));
                                item.setUrl(forecast.getString("url"));
                                item.setTS(forecast.getString("ts"));

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

        // シングルトンクラスで実行
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            /*
            // Settingsを押したときの処理
            case R.id.action_settings:
                return true;
                break;
            */
            // シェアを押したときの処理
            case R.id.action_shere:
                makeShere();
                break;

            // 更新を押したときの処理
            case R.id.action_reload:
                // アクティビティスタックを破棄
                finish();
                // アクティビティ開始
                startActivity(new Intent(this, MainActivity.class));
                // アクティビティ移行時のアニメーションを無効化
                overridePendingTransition(0, 0);
                break;

            // アプリを評価
            case R.id.action_play:
                OpenPlay OpenPlay = new OpenPlay(this);
                OpenPlay.makeIntent();
                return true;

            // 戻る（<-）を押したときの処理
            case android.R.id.home:
                makeBackFromFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // シェアの挙動
    public void makeShere() {
        String articleTitle;
        String articleURL;
        // リストをクリックしたあと
        if (item != null) {
            articleTitle = item.getTitle();
            articleURL = item.getUrl();
        }
        // リストページ
        else {
            articleTitle = app_titile;
            articleURL = play_url;
        }
        // 文字結合
        String sharedText = articleTitle + " " + articleURL;
        // builderの生成　ShareCompat.IntentBuilder.from(Context context);
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(this);
        // アプリ一覧が表示されるDialogのタイトルの設定
        builder.setChooserTitle("シェアする？");
        // シェアするタイトル
        builder.setSubject(articleTitle);
        // シェアするテキスト
        builder.setText(sharedText);
        // シェアするタイプ（他にもいっぱいあるよ）
        builder.setType("text/plain");
        // Shareアプリ一覧のDialogの表示
        builder.startChooser();
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

            // Fragment終了
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (fragmentTransaction != null) {
                fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.framelayout1));
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                fragmentTransaction.commit();
            }

            // MainActivity LinearLayout表示
            findViewById(R.id.layout_list).setVisibility(View.VISIBLE);

            // フラグをfalse
            ThisIsFlagment = false;

            // アクションバーの戻る(<-)を消す
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            // アクションバーのタイトルを戻す
            getSupportActionBar().setTitle(R.string.app_name);
            getSupportActionBar().setSubtitle(null);
        }
        // アクティビティ終了
        else{
            finish();
        }
    }

}