package com.example.yako.httpclient;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.net.http.AndroidHttpClient;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
public class MainActivity extends ActionBarActivity {

    private String str;
    private String title;
    private String fileName = "testfile.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // タイトルを取得

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    // 大阪の天気予報XMLデータ
                    HttpGet httpGet = new HttpGet("http://9post.jp/new-post");
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    str = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                    Log.d("Thread", "sleep");

                    // ファイルへの書き込み
                    saveFile(fileName, str);

                    Thread.sleep(5000);
                    Log.d("HTTP", str);
                } catch(Exception ex) {
                    System.out.println(ex);
                    Log.d("HTTP", "Exception");
                }

            }
        }).start();


        String str_read = readFile(fileName);
        Log.d("readFile", str_read);


    }

    // ファイルを保存
    public void saveFile(String file, String str) {
        FileOutputStream fileOutputstream = null;

        try {
            fileOutputstream = openFileOutput(file, Context.MODE_PRIVATE);
            fileOutputstream.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // ファイルを読み出し
    public String readFile(String file) {
        FileInputStream fileInputStream;
        String text = null;

        try {
            fileInputStream = openFileInput(file);
            String lineBuffer = null;

            BufferedReader reader= new BufferedReader(new InputStreamReader(fileInputStream,"UTF-8"));
            while( (lineBuffer = reader.readLine()) != null ) {
                text = lineBuffer ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
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
