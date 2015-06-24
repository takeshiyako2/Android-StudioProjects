package com.example.yako.myapplicationactivitytest;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;

public class Test2 extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        Button jp_button = (Button)findViewById(R.id.jp_button);
        Button en_button = (Button)findViewById(R.id.en_button);
        final TextView msg_text = (TextView)findViewById(R.id.msg_text);

        jp_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                msg_text.setText("こんにちは");
            }
        });

        en_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                msg_text.setText("Hello");
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test2, menu);
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
