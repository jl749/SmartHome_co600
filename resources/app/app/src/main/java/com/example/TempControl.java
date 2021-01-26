package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TempControl extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temperature_control);
        TextView temperature = (TextView)findViewById(R.id.CurrentTemp);
        temperature.setText(((MyApplication) this.getApplication()).getTemperature());
        TextView humidity = (TextView)findViewById(R.id.CurrentHumid);
        humidity.setText(((MyApplication) this.getApplication()).getHumidity());
    }
}
