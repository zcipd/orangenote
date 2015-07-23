package com.orangenote.orangenote;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by min on 15. 7. 11.
 */
public class notification extends Activity {
    private AlarmManager mManager;
    private Calendar c = Calendar.getInstance();


    private DatePicker mDate;
    private TimePicker mTime;
    private int mProgressStatus = 0;
    private int NOTI_ID = 101;//통지관련
    private MediaPlayer sound = new MediaPlayer();

    int  myYear=0, myMonth=0, myDay=0,myHour=0, myMinute=0,Alarm_delete=0;

    Button TimeButton;
    Button DateButton;

    public notification() {
    }


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //통지 매니져 획득

        mManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);//알람 매너져를 획득

        sound = MediaPlayer.create(this,R.raw.alram);


        setContentView(R.layout.alarm_layout);
        Button okbutton = (Button)findViewById(R.id.ok_alarm_button);



        okbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*
                SharedPreferences settings_notification = getSharedPreferences("notification_position", 0); //공유 프레퍼런스 만들기 passwordSharedPreferences.Editor editor_password = settings_notification.edit();
                SharedPreferences.Editor editor_password = settings_notification.edit();
                String notifi = Integer.toString(nRowID);
                editor_password.putString("notification_position",notifi);
                editor_password.commit();
                */
                Alarm_delete =0;
                setAlarm();
                finish();
            }
        });


        DateButton =(Button)findViewById(R.id.Date_button);
        DateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int yYear = c.get(Calendar.YEAR);
                int yMonth = c.get(Calendar.MONTH);
                int yDay = c.get(Calendar.DAY_OF_MONTH);
                Dialog dialogDate = new DatePickerDialog(notification.this, myDateSetListener, yYear, yMonth, yDay);
                dialogDate.show();
            }
        });
        TimeButton = (Button)findViewById(R.id.Time_button);
        TimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int Hour = c.get(Calendar.HOUR_OF_DAY);
                int min = c.get(Calendar.MINUTE);
                Dialog dialogTime = new TimePickerDialog(notification.this, myTimeSetListener, Hour, min, false);
                dialogTime.show();

            }
        });
    }
    //알람 설정
    private void setAlarm(){

        Calendar nowDate = Calendar.getInstance();
        int noDate_month = nowDate.get(Calendar.MONTH);
        nowDate.set(Calendar.MONTH,noDate_month+1);
        nowDate.set(Calendar.SECOND,00);
        //sound.start();
        //c.set(myYear, myMonth, myDay, myHour,myMinute,0);
        c.set(Calendar.YEAR, myYear);
        c.set(Calendar.MONTH, myMonth);
        c.set(Calendar.DAY_OF_MONTH, myDay);
        c.set(Calendar.HOUR_OF_DAY, myHour);
        c.set(Calendar.MINUTE, myMinute);
        c.set(Calendar.SECOND, 00);
        /*Date date = new Date();
        date.setYear(myYear);
        date.setMonth(myMonth);
        date.setDate(myDay);
        date.setHours(myHour);
        date.setMinutes(myMinute);
        date.setSeconds(0);
        c.setTime(date);*/
        int i = 0;
        int select=0;
        final long electtim = c.getTimeInMillis() - nowDate.getTimeInMillis()  ;
        System.out.println(electtim);
        // Toast.makeText(getApplicationContext(),electtim,Toast.LENGTH_SHORT).show();

        new Thread(new Runnable(){
            public void run() {

                try{
                    Thread.sleep(electtim);
                    noti();
                }catch(InterruptedException e){

                }


            }
        }).start();

       /* Notification noti = new Notification(R.mipmap.ic_launcher,"Nomal Notification",5000);
        noti.flags = Notification.FLAG_AUTO_CANCEL;
        noti.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        noti.number = 13;
        noti.setLatestEventInfo(this, "Nomal Title", "Nomal Summary", Pending);
        mNotification.notify(1234, noti);*/

    }
    public void noti(){
        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent Pending = PendingIntent.getActivity(notification.this,0,intent,0);
       /* mManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), Pending);*/

        if(Alarm_delete==0) {
            NotificationManager mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            Notification.Builder noti = new Notification.Builder(this);
            noti.setSmallIcon(R.mipmap.ic_launcher);
            noti.setTicker("뭘봐 ");
            noti.setWhen(System.currentTimeMillis());
            noti.setNumber(10);
            noti.setContentText("이건뭐지 ");
            noti.setContentTitle("쓸게많아");
            noti.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
            noti.setContentIntent(Pending);
            noti.setAutoCancel(true);
            noti.setPriority(NotificationCompat.PRIORITY_MAX);
            noti.setAutoCancel(true);
            mNotification.notify(1313, noti.build());
        }
        else if(Alarm_delete==1){
            NotificationManager mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            mNotification.cancel(1313);
        }

    }
    private TimePickerDialog.OnTimeSetListener myTimeSetListener = new TimePickerDialog.OnTimeSetListener(){
        public void onTimeSet(TimePicker view, int hourOfDay, int minute){
            String time = "Hour : " + hourOfDay + "Minute : " + minute;
            //String time = "Hour : " + hourOfDay + "Minute : " + minute;
            Toast.makeText(notification.this,time,Toast.LENGTH_SHORT).show();
            myHour = hourOfDay;
            myMinute = minute;
        }
    };
    private DatePickerDialog.OnDateSetListener myDateSetListener = new DatePickerDialog.OnDateSetListener(){
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            String date = "Year : " + year + "Month : " + monthOfYear+ "Day : " + dayOfMonth;
            // String date = "Year : " + myYear + "Month : " + myMonth+ "Day : " + myDay;
            myYear=year;
            myMonth = monthOfYear+1;
            myDay =dayOfMonth;
            Toast.makeText(notification.this,date,Toast.LENGTH_SHORT).show();

        }
    };
}
