package monst.post.nine.listview1;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 表示用のリストを用意
        List<RowDetail> objects = new ArrayList<RowDetail>();
        for (int i = 0; i < 100; i++) {
            RowDetail item = new RowDetail();

            StringBuilder title = new StringBuilder();
            title.append("テスト");
            title.append(i);
            String title_message = new String(title);

            StringBuilder detail = new StringBuilder();
            detail.append(title_message);
            detail.append("の詳細データです。");
            String detail_message = new String(detail);

            item.setTitle(title_message);
            item.setDetail(detail_message);

            // リストにアイテムを追加
            objects.add(item);
        }

        // リストビューへ紐付け
        listview = (ListView)findViewById(R.id.listView);

        // ArrayAdapterを設定
        RowDetailAdapter rowAdapater = new RowDetailAdapter(this, 0, objects);

        // リストビューへリストを登録
        listview.setAdapter(rowAdapater);

        // リストビューがクリックされたときのコールバックリスナーを登録
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // リストのアイテムをクリックした時の挙動
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // リストビューの項目を取得
                ListView listview = (ListView) parent;
                RowDetail item = (RowDetail) listview.getItemAtPosition(position);
                String text = "クリックしました:" + item.getTitle() + ":" + item.getDetail();

                // トースト表示
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });

        // リストビューの項目が長押しされたときのコールバックリスナーを登録
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                // リストビューの項目を取得
                ListView listview = (ListView) parent;
                RowDetail item = (RowDetail) listview.getItemAtPosition(position);
                String text = "長押ししました:" + item.getTitle() + ":" + item.getDetail();

                // トースト表示
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

                // onItemClickを実行しない
                return true;
            }
        });

    }

}
