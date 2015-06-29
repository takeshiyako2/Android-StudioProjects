package com.example.yako.sqlitetes;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    private MemoDao dao;
    private LinearLayout showData;
    private EditText dataEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SQLiteの準備
        MyDBHelper helper = new MyDBHelper(this, null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        //SQLiteDatabase db = helper.getWritableDatabase();
        dao = new MemoDao(db);

        // データ入力欄の初期設定
        dataEdit = (EditText)findViewById(R.id.dataEdit);
        dataEdit.setOnKeyListener(new AddressBarOnKeyListener());

        // データ出力Viewグループの取得
        showData = (LinearLayout)findViewById(R.id.showData);

        // 表示データの更新
        changeData();
    }


    /**
     * 追加ボタンのクリックイベント
     * @param view
     */
    public void addData(View view) {

        // データの追加
        dao.insert(dataEdit.getText().toString());

        // 入力欄のクリア
        dataEdit.setText(null);

        // 表示データの更新
        changeData();
    }


    /**
     * 削除ボタンのクリックイベント
     * @param view
     */
    public void deleteData(View view) {

        // データの削除1
        dao.delete(Integer.parseInt(dataEdit.getText().toString()));

        // 入力欄のクリア
        dataEdit.setText(null);

        // 表示データの更新
        changeData();
    }

    /**
     * 表示データの更新
     */
    private void changeData() {

        // 表示中のデータを一旦すべてクリアする。
        showData.removeAllViews();

        // DBからすべてのデータを取得する。
        List<MyDBEntity> entityList = dao.findAll();

        // データを表示領域に追加する
        for(MyDBEntity entity: entityList) {
            TextView textView = new TextView(this);
            textView.setText(entity.getRowId() + "： " + entity.getValue());
            showData.addView(textView);
        }
    }


    // ソフトキーボードの「確定」が押された時にソフトキーボードを消す
    private class AddressBarOnKeyListener implements OnKeyListener {

        public boolean onKey(View view, int keyCode, KeyEvent event) {

            //EnterKeyが押されたかを判定
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && keyCode == KeyEvent.KEYCODE_ENTER) {

                //ソフトキーボードを閉じる
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                return true;
            }

            return false;
        }
    }

/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
*/
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
