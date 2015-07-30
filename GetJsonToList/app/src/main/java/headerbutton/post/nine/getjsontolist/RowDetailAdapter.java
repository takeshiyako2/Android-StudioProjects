package headerbutton.post.nine.getjsontolist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;
import android.graphics.Typeface;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;


public class RowDetailAdapter extends ArrayAdapter<RowDetail> {

    private LayoutInflater layoutinflater;
    private RequestQueue mQueue;
    private ImageLoader mImageLoader;

    // コンストラクタ
    public RowDetailAdapter(Context context, int textViewResourceId, List<RowDetail> objects){
        super(context, textViewResourceId, objects);

        // xmlで定義したレイアウトを取得
        layoutinflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Volleyのキュー
        mQueue = Volley.newRequestQueue(getContext());

        // イメージローダーにキューとキャッシュ設定を渡す
        mImageLoader = new ImageLoader(mQueue, new BitmapCache());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        // 指定行のデータを取得
        RowDetail detail = (RowDetail)getItem(position);

        Log.d("getView", "convertView: " + convertView);

        // nullの場合のみ再作成
        if(null == convertView){
            convertView = layoutinflater.inflate(R.layout.row, null);
        }else {

        }

        // 行のデータを項目へ設定
        TextView text1 = (TextView)convertView.findViewById(R.id.textViewTitle);
        text1.setText(detail.getTitle());

        // 画像取得処理
        imageView = (ImageView) convertView.findViewById(R.id.image);
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.drawable.image_load , android.R.drawable.ic_delete);
        mImageLoader.get(detail.getImage(), listener);
        Log.d("getView", "image: " + detail.getImage());

        // 返却
        return convertView;
    }

}