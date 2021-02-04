package com.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.widget.SwitchCompat;

public class Alarm extends Activity {

    static Boolean isTouched = false;
    private static BroadcastReceiver tickReceiver;
    final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_page);
        if(!((MyApplication) this.getApplication()).checkNull()) {
            SwitchCompat aSwitch = findViewById(R.id.toggleAlarm);
            update();
            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SetAlarm alarm = new SetAlarm();
                    if (isChecked) {
                        ImageView image = (ImageView) findViewById(R.id.alarmState1);
                        image.setImageResource(R.drawable.alarmon);
                        TextView state = (TextView) findViewById(R.id.alarmState);
                        state.setText("Alarm Armed");
                        alarmOn();
                        alarm.run(true, 1234);
                    } else {
                        ImageView image = (ImageView) findViewById(R.id.alarmState1);
                        image.setImageResource(R.drawable.alarmoff);
                        TextView state = (TextView) findViewById(R.id.alarmState);
                        state.setText("Alarm Disarmed");
                        alarmOff();
                        alarm.run(false, 1234);
                    }
                }
            });
            if (((MyApplication) this.getApplication()).getAlarm().equals("True")) {
                ImageView image = (ImageView) findViewById(R.id.alarmState1);
                TextView state = (TextView) findViewById(R.id.alarmState);
                aSwitch.setChecked(true);
                image.setImageResource(R.drawable.alarmon);
                state.setText("Alarm Armed");

            } else {
                ImageView image = (ImageView) findViewById(R.id.alarmState1);
                TextView state = (TextView) findViewById(R.id.alarmState);
                aSwitch.setChecked(false);
                image.setImageResource(R.drawable.alarmoff);
                state.setText("Alarm Disarmed");
            }
            tickReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                        update();
                    }
                }
            };
            registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        }
        else{
            AlertDialog alertDialog = new AlertDialog.Builder(Alarm.this).create();
            alertDialog.setTitle("Connection Error");
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setMessage("Connection could no be established with the server. Please try again.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            end();
                        }
                    });
            alertDialog.show();
        }
    }

    public void end(){
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void checkConnection() {
        if (((MyApplication) this.getApplication()).connection()) {
            AlertDialog alertDialog = new AlertDialog.Builder(Alarm.this).create();
            alertDialog.setTitle("Connection Error");
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setMessage("Connection could no be established with the server. Please try again.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            end();
                        }
                    });
            alertDialog.show();
        }
    }

    public void update(){
        UpdateValues u = new UpdateValues();
        u.run(((MyApplication) this.getApplication()));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkConnection();
            }
        }, 2500);
        System.out.println(((MyApplication) this.getApplication()).getTemperature());
    }

    public void alarmOn(){
        ((MyApplication) this.getApplication()).setAlarm("True");
    }
    public void alarmOff(){
        ((MyApplication) this.getApplication()).setAlarm("False");
    }

    @Override
    public void onStop()
    {
        super.onStop();
        //unregister broadcast receiver.
        if(tickReceiver!=null)
            unregisterReceiver(tickReceiver);
    }
}
