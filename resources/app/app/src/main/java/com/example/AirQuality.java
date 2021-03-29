package com.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

/**Air Quality page class
 * This activity is called when the user navigates to the AQI Page
 */

public class AirQuality extends Activity {

    public int airQualityIndex;
    public String apiKey;
    private static BroadcastReceiver tickReceiver;

    /*
    *Sets view to airquality page loads all values and sets onclick listeners unless
    *null values found. If null values found show connection lost error message.
    *Sets up a loop that checks connection loss and updates ui every minute.
    */
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

    /*Checks if connection lost and updates UI (every minute)*/
    private void update(){
        checkConnection();
        updateUI();
    }

    /*Updates Ui */
    private void updateUI(){
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

    /*Runs connection lost alert if null values found*/
    private void checkConnection() {
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

    /*Mutator for the fan global variable */
    private void setFanOn(){
        ((MyApplication) this.getApplication()).setFan("1");
    }
    private void setFanOff(){
        ((MyApplication) this.getApplication()).setFan("0");
    }

    /*Restarts app when connection lost*/
    private void end(){
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(i);
    }

    /*Changes UI depending on the air quality picked up from censor*/
    private void changeStatus(){
        TextView good = findViewById(R.id.good);
        TextView moderate = findViewById(R.id.moderate);
        TextView unhealthy = findViewById(R.id.unhealthy);
        TextView hazardous = findViewById(R.id.hazardous);
        if(airQualityIndex<= 50){
            good.setBackgroundResource(R.drawable.bordergood);
        }
        else if(airQualityIndex>50 && airQualityIndex<=100){
            moderate.setBackgroundResource(R.drawable.bordermoderate);
        }
        else if(airQualityIndex>100 && airQualityIndex<=150){
            unhealthy.setBackgroundResource(R.drawable.borderunhealthy);
        }
        else{
            hazardous.setBackgroundResource(R.drawable.borderhazardous);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
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
