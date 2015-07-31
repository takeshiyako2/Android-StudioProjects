package headerbutton.post.nine.getjsontolist;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

public class OpenFacebookPage {

    private Context this_context;

    // コンストラクタ
    public OpenFacebookPage(Context context){
        this_context = context;
    }

    // FacebookページをIntentで開く
    public void makeIntent() {
        String facebookUrl = "https://www.facebook.com/9post";
        try {
            int versionCode = this_context.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                this_context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                ;
            } else {
                // open the Facebook app using the old method (fb://profile/id or fb://page/id)
                this_context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/9post")));
            }
        } catch (PackageManager.NameNotFoundException e) {
            // Facebook is not installed. Open the browser
            this_context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));

        }

    }
}
