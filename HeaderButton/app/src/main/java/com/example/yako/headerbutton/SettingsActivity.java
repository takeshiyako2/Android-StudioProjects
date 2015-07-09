package com.example.yako.headerbutton;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.util.Collections;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class SettingsActivity extends ActionBarActivity {

    private static String top_url = "http://9post.jp/";
    private static String menu1_url = "http://9post.jp/";
    private static String menu2_url = "http://9post.jp/ranking";
    //    private static String top_url = "https://www.google.co.jp/";
//    private static String menu1_url = "https://www.google.co.jp/";
//    private static String menu2_url = "https://www.facebook.com/";
    private MemoDao dao;
    private LinearLayout showData;
    private LinearLayout TextView;

    // Log
    private final static String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // アイコン
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(R.drawable.nine_post_icon_small);

        // SQLiteの準備
        MyDBHelper helper = new MyDBHelper(this, null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        //SQLiteDatabase db = helper.getWritableDatabase();
        dao = new MemoDao(db);

        // データ出力Viewグループの取得
//        showData = (LinearLayout)findViewById(R.id.showData);

        // 表示データの更新
//        changeData();

        // 最新データの表示
        newData();

        // トグル
        toggleData();

    }

    public void toggleData() {

        // トグル
        CompoundButton toggle = (CompoundButton)findViewById(R.id.toggle);

        // DBからすべてのデータを取得する。
        List<MyDBEntity> entityList = dao.findAll();

        // DBが空の場合
        if (entityList.isEmpty() == true) {
            // トグルのデフォルト
            toggle.setChecked(true);
        }
        else {
            // Listの最後の要素を取得
            MyDBEntity e = entityList.get(entityList.size() - 1);
            if (e.getValue().equals("true")) {
                toggle.setChecked(true);
            }
        }

        // トグルの状態変更 (トグル操作のリスナーを登録)
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // 状態が変更された
                if (isChecked) {
                    Toast.makeText(SettingsActivity.this, "ONにしました", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SettingsActivity.this, "OFFにしました", Toast.LENGTH_SHORT).show();
                }

                // データの追加
                dao.insert("" + isChecked);

                // 表示データの更新
//                changeData();
            }
        });
    }

    /**
     * 表示データの更新
     */
    private void changeData() {

        // 表示中のデータを一旦すべてクリアする。
        showData.removeAllViews();

        // DBからすべてのデータを取得する。
        List<MyDBEntity> entityList = dao.findAll();

        // リストを降順に並び替えます。
        Collections.reverse(entityList);

        // データを表示領域に追加する
        for(MyDBEntity entity: entityList) {
            TextView textView = new TextView(this);
            textView.setText(entity.getRowId() + "： " + entity.getValue());
            showData.addView(textView);
        }

        // 最新データの表示
        newData();

    }

    /**
     * 最新データを表示
     */
    private void newData() {

        String return_text;

        // DBからすべてのデータを取得する。
        List<MyDBEntity> entityList = dao.findAll();

        // DBが空の場合
        if (entityList.isEmpty() == true) {
            return_text = "true";
        }
        else {
            // Listの最後の要素を取得
            MyDBEntity e = entityList.get(entityList.size() - 1);
            return_text = e.getValue();
        }

//        TextView textView = (TextView)findViewById(R.id.txtView);
//        textView.setText(return_text);
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /***
     * アクションバーを押したとき
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        finish();
        switch (item.getItemId()) {
            case R.id.menu1:
                top_url = menu1_url;
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.menu2:
                top_url = menu2_url;
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***
     * 戻るボタン
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        finish();
    }

}
