package com.example.yako.intenttest;
import android.app.Application;

public class Common extends Application{
    // グローバルに扱う変数
    String ResourceUrl;        // リソースになるURL
    // 変数を初期化する
    public void init(){
        ResourceUrl = "http://kancolle.lovesoku.jp/";
    }
}