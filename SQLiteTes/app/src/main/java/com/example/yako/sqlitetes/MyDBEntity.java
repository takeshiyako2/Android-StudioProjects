package com.example.yako.sqlitetes;

/**
 * Created by yako on 6/29/15.
 */
public class MyDBEntity {

    private int rowId;

    private String value;

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public int getRowId() {
        return rowId;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
