package com.example.yako.navigation;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

/**
 * Created by yako on 7/6/15.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private Context alarmReceiverContext;
    private int notificationProvisionalId;

    @Override
    public void onReceive(Context context, Intent receivedIntent) {

        alarmReceiverContext = context;

        notificationProvisionalId = receivedIntent.getIntExtra("notificationId", 0);
        NotificationManager myNotification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = prepareNotification();
        myNotification.notify(notificationProvisionalId, notification);

    }

    private Notification prepareNotification(){


        // タイトルを取得



        // 通知

        Intent bootIntent =
                new Intent(alarmReceiverContext, MainActivity.class);
        PendingIntent contentIntent =
                PendingIntent.getActivity(alarmReceiverContext, 0, bootIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                alarmReceiverContext);
        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setTicker("ウェーイ!" + System.currentTimeMillis())
                .setContentTitle("ウェーイ!" + System.currentTimeMillis())
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
