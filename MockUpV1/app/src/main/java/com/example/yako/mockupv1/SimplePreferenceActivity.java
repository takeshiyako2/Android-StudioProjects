package com.example.yako.mockupv1;

/**
 * Created by yako on 6/30/15.
 */
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SimplePreferenceActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SimplePreferenceFragment()).commit();


    }
}