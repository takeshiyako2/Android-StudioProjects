package com.example.yako.navigation;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.WebView;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by yako on 7/6/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private Context alarmReceiverContext;
    private int notificationProvisionalId;
    private MemoDao dao;

    @Override
    public void onReceive(Context context, Intent receivedIntent) {




        // Settingsを確認 ONだったら通知
        Log.d("onReceive", newData(context));
        if (newData(context).equals("true")) {
            Log.d("onReceive", "start notification");

            String message = "test!";

            alarmReceiverContext = context;
            notificationProvisionalId = receivedIntent.getIntExtra("notificationId", 0);
            NotificationManager myNotification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = prepareNotification(message);
            myNotification.notify(notificationProvisionalId, notification);


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





        // 通知

        Intent bootIntent =
                new Intent(alarmReceiverContext, MainActivity.class);
        PendingIntent contentIntent =
                PendingIntent.getActivity(alarmReceiverContext, 0, bootIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                alarmReceiverContext);
        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setTicker(message)
                .setContentTitle(message)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent);

        NotificationCompat.BigPictureStyle pictureStyle =
                new NotificationCompat.BigPictureStyle(builder);
        pictureStyle.bigPicture(BitmapFactory.decodeResource(alarmReceiverContext.getResources(), R.drawable.nine_post_icon));

        return pictureStyle.build();


    }
}
