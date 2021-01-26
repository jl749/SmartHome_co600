package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.appcompat.widget.SwitchCompat;

public class Alarm extends Activity {
    static Boolean isTouched = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_page);
        SwitchCompat aSwitch = findViewById(R.id.toggleAlarm);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked) {
                    ImageView image = (ImageView) findViewById(R.id.alarmState1);
                    image.setImageResource(R.drawable.alarmon);
                    TextView state = (TextView) findViewById(R.id.alarmState);
                    state.setText("Alarm Armed");
                    alarmOn();
                }
                else {
                    ImageView image = (ImageView) findViewById(R.id.alarmState1);
                    image.setImageResource(R.drawable.alarmoff);
                    TextView state = (TextView) findViewById(R.id.alarmState);
                    state.setText("Alarm Disarmed");
                    alarmOff();
                }
            }
        });
        if(((MyApplication) this.getApplication()).getAlarm().equals("True")){
            ImageView image = (ImageView) findViewById(R.id.alarmState1);
            TextView state = (TextView) findViewById(R.id.alarmState);
            aSwitch.setChecked(true);
            image.setImageResource(R.drawable.alarmon);
            state.setText("Alarm Armed");

        }
        else{
            ImageView image = (ImageView) findViewById(R.id.alarmState1);
            TextView state = (TextView) findViewById(R.id.alarmState);
            aSwitch.setChecked(false);
            image.setImageResource(R.drawable.alarmoff);
            state.setText("Alarm Disarmed");
        }
    }

    public void alarmOn(){
        ((MyApplication) this.getApplication()).setAlarm("True");
    }
    public void alarmOff(){
        ((MyApplication) this.getApplication()).setAlarm("False");
    }
}
