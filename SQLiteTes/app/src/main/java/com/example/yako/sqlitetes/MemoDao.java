package com.example.yako.sqlitetes;

/**
 * Created by yako on 6/29/15.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MemoDao {

    // テーブルの定数
    private static final String TABLE_NAME = "memo";
    private static final String COLUMN_ID = "rowid";
    private static final String COLUMN_DATA = "data";
    private static final String[] COLUMNS = {COLUMN_ID, COLUMN_DATA};

    // SQLiteDatabase
    private SQLiteDatabase db;

    /**
     * コンストラクタ
     * @param db
     */
    public MemoDao(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * 全データの取得   ----------------①
     * @return
     */
    public List<MyDBEntity> findAll() {
        List<MyDBEntity> entityList = new  ArrayList<MyDBEntity>();
        Cursor cursor = db.query(
                TABLE_NAME,
                COLUMNS,
                null,
                null,
                null,
                null,
                COLUMN_ID);

        while(cursor.moveToNext()) {
            MyDBEntity entity = new MyDBEntity();
            entity.setRowId(cursor.getInt(0));
            entity.setValue(cursor.getString(1));
            entityList.add(entity);
        }

        return entityList;
    }

    /**
     * 特定IDのデータを取得   ----------------②
     * @param rowId
     * @return
     */
    public MyDBEntity findById(int rowId) {
        String selection = COLUMN_ID + "=" + rowId;
        Cursor cursor = db.query(
                TABLE_NAME,
                COLUMNS,
                selection,
                null,
                null,
                null,
                null);

        cursor.moveToNext();
        MyDBEntity entity = new MyDBEntity();
        entity.setRowId(cursor.getInt(0));
        entity.setValue(cursor.getString(1));

        return entity;
    }

    /**
     * データの登録   ----------------③
     * @param data
     * @return
     */
    public long insert(String value) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATA, value);
        return db.insert(TABLE_NAME, null, values);
    }

    /**
     * データの更新   ----------------④
     * @param rowid
     * @param date
     * @return
     */
    public int update(MyDBEntity entity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATA, entity.getValue());
        String whereClause = COLUMN_ID + "=" + entity.getRowId();
        return db.update(TABLE_NAME, values, whereClause, null);
    }

    /**
     * データの削除   ----------------⑤
     * @param rowId
     * @return
     */
    public int delete(int rowId) {
        String whereClause = COLUMN_ID + "=" + rowId;
        return db.delete(TABLE_NAME, whereClause, null);
    }


}
