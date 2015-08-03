package news.splatoon.com.splatoon;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
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

        // nullの場合のみ再作成
        if(null == convertView){
            convertView = layoutinflater.inflate(R.layout.row, null);
        }

        // 行のデータを項目へ設定
        Log.d("getView", "id: " + detail.getId());
//        TextView text1 = (TextView)convertView.findViewById(R.id.textViewId);
//        text1.setText(detail.getId());

        TextView text2 = (TextView)convertView.findViewById(R.id.textViewTitle);
        text2.setText(detail.getTitle());

        TextView text3 = (TextView)convertView.findViewById(R.id.textViewSiteTitle);
        text3.setText(detail.getSiteTitle());

        TextView text4 = (TextView)convertView.findViewById(R.id.textViewTS);
        text4.setText(detail.getTS());

        // Glideを設定
        String url = detail.getImage();
        if(null != url) {

            Log.d("getView", "image: " + detail.getImage());

            final ImageView myImageView;
            myImageView = (ImageView) convertView.findViewById(R.id.image);
            Glide.with(getContext())
                    .load(url)
                    .override(100, 100)
                    .into(myImageView);
        }

        // 返却
        return convertView;
    }

}