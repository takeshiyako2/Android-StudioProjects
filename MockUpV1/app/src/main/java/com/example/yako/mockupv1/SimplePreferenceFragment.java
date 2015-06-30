package com.example.yako.mockupv1;

/**
 * Created by yako on 6/30/15.
 */

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SimplePreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preference);
    }

}
