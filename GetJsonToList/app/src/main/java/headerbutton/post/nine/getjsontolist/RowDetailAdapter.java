package headerbutton.post.nine.getjsontolist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;

public class RowDetailAdapter extends ArrayAdapter<RowDetail> {

    private LayoutInflater layoutinflater;

    // コンストラクタ
    public RowDetailAdapter(Context context, int textViewResourceId, List<RowDetail> objects){
        super(context, textViewResourceId, objects);

        // xmlで定義したレイアウトを取得
        layoutinflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

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
        Log.d("getView", "image: " + detail.getImage());

        // Glideを設定
        final ImageView myImageView;
        myImageView = (ImageView) convertView.findViewById(R.id.image);
        String url = detail.getImage();
        Glide.with(getContext())
                .load(url)
                .override(1200, 1200)
                .into(myImageView);

        // 返却
        return convertView;
    }

}