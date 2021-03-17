package com.example;

import android.app.Activity;
import android.os.Bundle;

public class DisableAlarm extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void run(){
        String houseID = ((MyApplication) this.getApplication()).getCurrentHouse();
        ((MyApplication) this.getApplication()).setAlarm("False");
        SetAlarm alarm = new SetAlarm();
        alarm.run(false, Integer.parseInt(houseID));
    }
}
