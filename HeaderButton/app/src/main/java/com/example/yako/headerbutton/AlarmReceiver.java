package com.example.yako.headerbutton;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.WebView;
import android.widget.RemoteViews;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {

    private Context alarmReceiverContext;
    private int notificationProvisionalId;
    private MemoDao dao;
    private NotificationManager myNotification;
    private final static String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent receivedIntent) {

        Log.d(TAG, "onReceive");
        Log.d(TAG, newData(context));

        // Settingsを確認 falseじゃなかったら通知
        if (newData(context).equals("false")) {
            // 通知しない
        }
        else {
            // 通知設定
            alarmReceiverContext = context;
            notificationProvisionalId = receivedIntent.getIntExtra("notificationId", 0);
            myNotification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            // HttpClientの為にスレッド開始
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // HttpClientで情報取得
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet("http://9post.jp/new-post");
                        HttpResponse httpResponse = httpClient.execute(httpGet);
                        int status = httpResponse.getStatusLine().getStatusCode();
                        // HTTPステータスOKなら通知する
                        if (status == HttpStatus.SC_OK) {
                            String str = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                            // 通知
                            Notification notification = prepareNotification(str);
                            myNotification.notify(notificationProvisionalId, notification);
                        }
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }

                }
            }).start();
        }
    }

    /**
     * SQLiteの最新データを返す true/false
     */
    private String newData(Context context) {

        String return_text;

        // SQLiteの準備
        MyDBHelper helper = new MyDBHelper(context.getApplicationContext(), null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();
        dao = new MemoDao(db);

        // DBからすべてのデータを取得する。
        List<MyDBEntity> entityList = dao.findAll();

        // DBが空の場合
        if (entityList.isEmpty() == true) {
            return_text = "true";
        }
        else {
            // Listの最後の要素を取得
            MyDBEntity e = entityList.get(entityList.size() - 1);
            return_text = e.getValue();
        }

        return return_text;
    }


    private Notification prepareNotification(String message){
        // 細かい通知設定
        Intent bootIntent = new Intent(alarmReceiverContext, MainActivity.class);
        bootIntent.putExtra("Notification", "done!!!"); // MainActivityへ値を渡す
        PendingIntent contentIntent = PendingIntent.getActivity(alarmReceiverContext, 0, bootIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(alarmReceiverContext);
        builder.setSmallIcon(R.drawable.ic_stat_9)
                .setTicker(message)
                .setContentTitle("9ポスト")
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent);

        return builder.build();

    }
}