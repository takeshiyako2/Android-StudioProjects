package nine.post.monst;

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
    public void makeIntent() {
        String facebookUrl = "https://play.google.com/store/apps/details?id=com.monst.news2&hl=ja";
        this_context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));

    }
}