package com.example;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;


public class FunctionMenu extends Activity {

    final Handler handler = new Handler(Looper.getMainLooper());
    public String houseId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        houseId = getIntent().getStringExtra("HOUSE_SESSION_ID");
        if(houseId!= null){
            ((MyApplication) this.getApplication()).setCurrentHouse(houseId);
        }
        setContentView(R.layout.function_menu);
        ImageButton button1 = (ImageButton) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), TempControl.class);
                startActivity(i);
            }
        });
        ImageButton button2 = (ImageButton) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Security.class);
                startActivity(i);
            }
        });
        ImageButton button3 = (ImageButton) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Alarm.class);
                startActivity(i);
            }
        });
        ImageButton button4 = (ImageButton) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Voicerec.class);
                startActivity(i);
            }
        });
        ImageButton button5 = (ImageButton) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), AirQuality.class);
                startActivity(i);
            }
        });
        ImageButton button6 = (ImageButton) findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Settings.class);
                startActivity(i);
            }
        });
        System.out.println(((MyApplication) this.getApplication()).getCurrentHouse());
        UpdateValues u = new UpdateValues();
        u.run(((MyApplication) this.getApplication()));
        GetAlarmAndTemp at = new GetAlarmAndTemp();
        at.run(((MyApplication) this.getApplication()),Integer.parseInt(((MyApplication) this.getApplication()).getCurrentHouse()));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkConnection();
            }
        }, 5000);
    }

    public void end(){
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(i);
    }

    public void checkConnection(){
        System.out.println("Alarm is on: " + ((MyApplication) this.getApplication()).getAlarm());
        if(((MyApplication) this.getApplication()).connection()){
            final AlertDialog alertDialog = new AlertDialog.Builder(FunctionMenu.this).create();
            alertDialog.setTitle("Connection Error");
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
            alertDialog.setMessage("Connection could no be established with the server. Please try again.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            alertDialog.dismiss();
                            end();
                        }
                    });
            if(!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
        else {
            if(((MyApplication) this.getApplication()).getAlarm().equals("True")){ //start alarm
                Intent startAlarm = new Intent(getApplicationContext(), AlarmReceiver.class);
                startAlarm.putExtra("HouseID",houseId);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, startAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), 60000, pendingIntent);
            }
            else{
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent stopAlarm = new Intent(getApplicationContext(), AlarmReceiver.class);
                stopAlarm.putExtra("HouseID",houseId);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, stopAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
                System.out.println("ALARM TURNED OFF");
            }
        }
    }
}
