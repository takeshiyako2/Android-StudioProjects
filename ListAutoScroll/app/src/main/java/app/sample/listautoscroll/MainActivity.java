package app.sample.listautoscroll;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {

    // 1ページ辺りの項目数
    Integer per_page = 20;

    // フッターのプログレスバー（クルクル）
    View mFooter;

    // 予報表示用リストビューのアダプター
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // リスト用のアダプターを準備
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        // アダプターにアイテムを追加します
        for (int i = 0; i < per_page; i++) {
            adapter.add("リストビュー：" + i);
        }

        // リストビューへ紐付け
        ListView listview = (ListView)findViewById(R.id.listView);

        // リストビューにアダプターを設定します
        listview.setAdapter(adapter);

        // リストビューにフッターを追加
        listview.addFooterView(getFooter());

        // スクロールのリスナー
        listview.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                // 最初とスクロール完了したとき
                if ((totalItemCount - visibleItemCount) == firstVisibleItem) {

                    // アイテムの数 フッター分の1を引く
                    Integer ItemCount = totalItemCount - 1;

                    // アダプターにアイテムを追加します
                    for (int i = ItemCount; i < (ItemCount + per_page); i++) {
                        adapter.add("リストビュー：" + i);
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
            }
        });
    }

    private View getFooter() {
        if (mFooter == null) {
            mFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);
        }
        return mFooter;
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
