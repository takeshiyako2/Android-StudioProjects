package com.pokemongo.news.pokemongo;

/**
 * Created by yako on 7/20/16 AD.
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;


public class OpenPlay {

    private Context this_context;

    // コンストラクタ
    public OpenPlay(Context context){
        this_context = context;
    }

    // PlayをIntentで開く
    public void makeIntent(String play_url) {
        this_context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(play_url)));

    }
}