package com.example;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {

    final Handler handler = new Handler(Looper.getMainLooper());
    private static final String CHANNEL_ID = "intruder";
    private String houseID;


    @Override
    public void onReceive(final Context context, Intent intent) {
        houseID = intent.getStringExtra("HouseID");

        UpdateValues uv = new UpdateValues();
        uv.run(((MyApplication)context.getApplicationContext()));
        GetAlarmAndTemp gat = new GetAlarmAndTemp();
        gat.run(((MyApplication)context.getApplicationContext()),houseID);
        updateWeather(context);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkIntruder(context);
                updateDataset(context);
            }
        }, 5000);
    }

    public void updateWeather(Context context){
        System.out.println(((MyApplication) context.getApplicationContext()).validPostCode());
        if(((MyApplication) context.getApplicationContext()).validPostCode()) {
            WeatherAPI wapi = new WeatherAPI();
            wapi.run(((MyApplication) context.getApplicationContext()), ((MyApplication)context.getApplicationContext()).getPostCode());
        }
    }

    public void updateDataset(Context c){
        boolean status = ((MyApplication)c.getApplicationContext()).getDataminingStatus();
        String apiKey = ((MyApplication)c.getApplicationContext()).getAPIKey();
        String[] arr = new String[5];
        arr[0] = ((MyApplication)c.getApplicationContext()).getTempOutside();
        arr[1] = ((MyApplication)c.getApplicationContext()).getTemperature();
        arr[2] = "?";
        arr[3] = ((MyApplication)c.getApplicationContext()).getHumidity();
        arr[4] = classifyTime();
        System.out.println(Arrays.toString(arr));
        String action = "";
        if(status){
            try {
                DataMining.GET().updateDataset(((MyApplication)c.getApplicationContext()).getUsername());
                DataMining.GET().buildModel();
            if(DataMining.GET().getNumInstances(((MyApplication)c.getApplicationContext()).getUsername())>9) {
                    if (DataMining.GET().classifyInst(arr) != null) {
                        action = DataMining.GET().classifyInst(arr);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (action.equals("C")) {
                coolingOn(apiKey);
            } else if (action.equals("H")) {
                heatingOn(apiKey);
            }
        }
        System.out.println(action);
    }

    public void coolingOn(String apiKey){
        FanLedLockControl flc = new FanLedLockControl();
        flc.run("1", true,apiKey,"FAN");
        flc.run("", false,apiKey,"HEAT");
        System.out.println("cooling on");
    }

    public void heatingOn(String apiKey){
        FanLedLockControl flc = new FanLedLockControl();
        flc.run("1", false,apiKey,"FAN");
        flc.run("", true,apiKey,"HEAT");
        System.out.println("Heating on");
    }

    public void checkIntruder(Context c){
        boolean intruder = ((MyApplication)c.getApplicationContext()).getIntruder();
        String alarm = ((MyApplication)c.getApplicationContext()).getAlarm();

        Intent dial = new Intent(Intent.ACTION_DIAL);
        dial.setData(Uri.parse("tel:999"));
        dial.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(c, 0, dial, 0);

        PendingIntent dismissIntent = CloseNotification.getDismissIntent(1, c);

        if (intruder && alarm.equals("1")){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(c, CHANNEL_ID)
                    .setSmallIcon(R.drawable.mainicon)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentTitle("Intrusion Alarm.")
                    .setContentText("Intruder has been detected in house: "+houseID)
                    .setAutoCancel(true)
                    .addAction(0, "Call Police", intent)
                    .addAction(0, "Dismiss", dismissIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_ALARM);
            System.out.println("INTRUDER ALERT!");
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(c);
            notificationManager.notify(1, builder.build());
            }
    }

    public String classifyTime(){
        Calendar rightNow = Calendar.getInstance();
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
        if(currentHourIn24Format >= 6 && currentHourIn24Format <=10){
            return "6~10";
        }
        else if(currentHourIn24Format >= 11 && currentHourIn24Format <=13){
            return "11~13";
        }
        else if(currentHourIn24Format >= 14 && currentHourIn24Format <=17){
            return "14~17";
        }
        else if(currentHourIn24Format >= 18 && currentHourIn24Format <=22){
            return "18~22";
        }
        else{
            return "others";
        }
    }
}
