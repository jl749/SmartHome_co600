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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;


public class AirQuality extends Activity {

    public int airQualityIndex;
    public String apiKey;
    final Handler handler = new Handler(Looper.getMainLooper());
    private static BroadcastReceiver tickReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.airquality_page);
        if (!((MyApplication) this.getApplication()).checkNull()) {
            SwitchCompat fanSwitch = findViewById(R.id.fanSwitch);
            updateUI();
            changeStatus();
            apiKey = ((MyApplication) this.getApplication()).getAPIKey();
            final FanLedLockControl fllc = new FanLedLockControl();
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
        }
        else{
            final AlertDialog alertDialog = new AlertDialog.Builder(AirQuality.this).create();
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

    public void update(){
        checkConnection();
        updateUI();
    }

    public void updateUI(){
        TextView aqi = findViewById(R.id.aqi);
        SwitchCompat fanSwitch = findViewById(R.id.fanSwitch);
        airQualityIndex = Integer.parseInt(((MyApplication) this.getApplication()).getAqi());
        aqi.setText(((MyApplication) this.getApplication()).getAqi());
        if (((MyApplication) this.getApplication()).getFan().equals("1")) {
            fanSwitch.setChecked(true);
        } else {
            fanSwitch.setChecked(false);
        }
    }

    public void checkConnection() {
        if (((MyApplication) this.getApplication()).connection()) {
            AlertDialog alertDialog = new AlertDialog.Builder(AirQuality.this).create();
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

    public void setFanOn(){
        ((MyApplication) this.getApplication()).setFan("1");
    }
    public void setFanOff(){
        ((MyApplication) this.getApplication()).setFan("0");
    }

    public void end(){
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(i);
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

    @Override
    public void onStop()
    {
        super.onStop();
        //unregister broadcast receiver.
        if(tickReceiver!=null)
            unregisterReceiver(tickReceiver);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(AirQuality.this, FunctionMenu.class);
        startActivity(i);
        finish();
    }

}
