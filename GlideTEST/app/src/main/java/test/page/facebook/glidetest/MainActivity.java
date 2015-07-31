package test.page.facebook.glidetest;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ビューを取得
        ImageView imageView = (ImageView) findViewById(R.id.my_image_view);

        // Glideを設定
        Glide.with(this)
                // 画像URL
                .load("https://40.media.tumblr.com/f49e56a443aecd533fb53d55a1cf1408/tumblr_nsc4fht5ol1u3hv5ko1_1280.jpg")
                // リスナー（エラーハンドリングをする）
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String s, Target<GlideDrawable> glideDrawableTarget, boolean b) {
                        Log.d("Glide", "Error in Glide listener");
                        if (e != null) {
                            e.printStackTrace();
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable glideDrawable, String s, Target<GlideDrawable> glideDrawableTarget, boolean b, boolean b2) {
                        return false;
                    }
                })
                // リサイズ（縦横の最大サイズを指定して、収める）
                .override(600, 600)
                // ローディング画像
                .placeholder(android.R.drawable.ic_menu_call)
                // エラー画像
                .error(android.R.drawable.ic_delete)
                // placeholderを設定した場合に必要 これを書かないとplaceholder画像と同じ大きさにリサイズされる
                .dontAnimate()
                // imageViewに投入
                .into(imageView);

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
