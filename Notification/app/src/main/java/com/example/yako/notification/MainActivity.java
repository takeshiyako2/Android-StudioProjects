package com.example.yako.notification;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.TextView;
import java.util.Calendar;


public class MainActivity extends Activity {

    TimePicker tPicker;
    int notificationId;
    private PendingIntent alarmIntent;
    private String nextalarm;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tPicker  =  (TimePicker)findViewById(R.id.timePicker);
        tPicker.setIs24HourView(true);
        Calendar setTimeCalendar = Calendar.getInstance();
        tPicker.setCurrentHour(setTimeCalendar.get(Calendar.HOUR_OF_DAY));

        Button startBtn = (Button)findViewById(R.id.start);
        Button stopBtn = (Button)findViewById(R.id.stop);

        stopBtn.setOnClickListener(myAlarmListener);
        startBtn.setOnClickListener(myAlarmListener);

        /*
        Resources res = getResources();
        int lightBlue = res.getColor(R.color.lightBlue);

        startBtn.setTextColor(lightBlue);
        stopBtn.setTextColor(lightBlue);
*/
    }


    View.OnClickListener myAlarmListener= new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent bootIntent = new Intent(MainActivity.this, AlarmBroadcastReceiver.class);
            bootIntent.putExtra("notificationId", notificationId);
            alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, bootIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

            TextView textView = (TextView) findViewById(R.id.text_content);

            switch (v.getId()) {
                case R.id.start:

                    int hour = tPicker.getCurrentHour();
                    int minute = tPicker.getCurrentMinute();

                    Calendar startTime = Calendar.getInstance();
                    startTime.set(Calendar.HOUR_OF_DAY, hour);
                    startTime.set(Calendar.MINUTE, minute);
                    startTime.set(Calendar.SECOND, 0);
                    long alarmStartTime = startTime.getTimeInMillis();

                    // リピート
                    alarm.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            alarmStartTime,
                            1 * 1000 * 60,
                            alarmIntent
                    );
                    Toast.makeText(MainActivity.this, "通知セット完了!", Toast.LENGTH_SHORT).show();
                    textView.setText("通知セット");
                    notificationId++;

                    break;
                case R.id.stop:
                    alarm.cancel(alarmIntent);
                    Toast.makeText(MainActivity.this, "通知をキャンセルしました!", Toast.LENGTH_SHORT).show();
                    textView.setText("通知をキャンセル");
                    break;
            }
        }
    };
}