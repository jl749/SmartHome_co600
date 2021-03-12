package com.example;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


public class AirQuality extends Activity {

    public int airQualityIndex;
    public String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.airquality_page);
        airQualityIndex = Integer.parseInt(((MyApplication) this.getApplication()).getAqi());
        TextView aqi = findViewById(R.id.aqi);
        if (!((MyApplication) this.getApplication()).checkNull()) {
            apiKey = ((MyApplication) this.getApplication()).getAPIKey();
            final FanLedLockControl fllc = new FanLedLockControl();
            aqi.setText(((MyApplication) this.getApplication()).getAqi());
            Switch fanSwitch = findViewById(R.id.fanSwitch);
            fanSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        fllc.run("1", true,apiKey,"FAN");
                        setFanOn();
                    }
                    else{
                        fllc.run("1", false,apiKey,"FAN");
                        setFanOff();
                    }
                }
            });
            if (((MyApplication) this.getApplication()).getFan().equals("1")) {
                fanSwitch.setChecked(true);
            } else {
                fanSwitch.setChecked(false);
            }
            changeStatus();
        }
    }

    public void setFanOn(){
        ((MyApplication) this.getApplication()).setFan("1");
    }
    public void setFanOff(){
        ((MyApplication) this.getApplication()).setFan("0");
    }

    public void changeStatus(){
        TextView good = findViewById(R.id.good);
        TextView moderate = findViewById(R.id.moderate);
        TextView unhealthy = findViewById(R.id.unhealthy);
        TextView hazardous = findViewById(R.id.hazardous);
        if(airQualityIndex<= 25){
            good.setBackgroundResource(R.drawable.bordergood);
        }
        else if(airQualityIndex>25 && airQualityIndex<=50){
            moderate.setBackgroundResource(R.drawable.bordermoderate);
        }
        else if(airQualityIndex>50 && airQualityIndex<=75){
            unhealthy.setBackgroundResource(R.drawable.borderunhealthy);
        }
        else{
            hazardous.setBackgroundResource(R.drawable.borderhazardous);
        }
    }
}
