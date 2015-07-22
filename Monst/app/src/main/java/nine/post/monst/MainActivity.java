package nine.post.monst;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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



public class MainActivity extends ActionBarActivity {

    // JSONデータ取得URL
    private String BASE_URL_API = "http://api.monst.hitokoto.co/feed.json";
    private String URL_API;

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

    // 予報表示用リストビューのアダプター
    RowDetailAdapter rowAdapater;

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

                // get item
                ListView listview = (ListView) parent;
                RowDetail item = (RowDetail) listview.getItemAtPosition(position);

                //

                findViewById(R.id.layout_list).setVisibility(View.GONE);

                // Fragment

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                WebviewFragment fragment = new WebviewFragment();

//                Bundle bundle = new Bundle();
//                bundle.putString("url", item.getUrl());
//                WebviewFragment.setArguments(bundle);

                fragmentTransaction.add(R.id.layout_fragment_1, fragment, "tag");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();


                /*
                // リストビューの項目を取得
                ListView listview = (ListView) parent;
                RowDetail item = (RowDetail) listview.getItemAtPosition(position);
                String text = "クリックしました:" + item.getTitle() + ":" + item.getUrl();

                // トースト表示
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                */


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

    }

    // クルクル
    private View getFooter() {
        if (mFooter == null) {
            mFooter = getLayoutInflater().inflate(R.layout.listview_footer,
                    null);
        }
        return mFooter;
    }

    // リクエスト処理
    private void request() {
        // ロードダイアログ表示
//        final ProgressDialog pDialog = new ProgressDialog(this);
//        pDialog.setMessage("Loading...");
//        pDialog.show();

        Log.d(TAG, "request URL_API: " + URL_API);

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.GET, URL_API, null,
                new Response.Listener<JSONArray>() {
                    // レスポンス受信のリスナー
                    @Override
                    public void onResponse(JSONArray response) {
                        // ログ出力
                        Log.d(TAG, "request onResponse: " + response.toString());

                        // ロードダイアログ終了
//                        pDialog.hide();

                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject forecast = response.getJSONObject(i);

                                // JSONから取得
                                String id = forecast.getString("id");
                                String title = forecast.getString("title");
                                String site_title = forecast.getString("site_title");
                                String url = forecast.getString("url");
                                String ts = forecast.getString("ts");

                                // リストにアイテムを追加
                                RowDetail item = new RowDetail();
                                item.setId(id);
                                item.setTitle(title);
                                item.setSiteTitle(site_title);
                                item.setUrl(url);
                                item.setTS(ts);

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
                        // ロードダイアログ終了
//                        pDialog.hide();

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