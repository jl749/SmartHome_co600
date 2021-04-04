package com.example;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;


/**Function menu page class
 * This activity is called when the user logs in and enters pin (and chooses house).
 */
public class FunctionMenu extends Activity {

    final Handler handler = new Handler(Looper.getMainLooper());
    public String houseId;
    private ProgressDialog working_dialog;

    /*Sets on click listeners for the menu.
    * Checks all connections, loads data and sets up 1 minute main loop that updates values of censors.
    * Shows connection lost alert message if connection failed.
    */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        houseId = getIntent().getStringExtra("HOUSE_SESSION_ID");
        setAPIKey();
        if(houseId!= null){
            ((MyApplication) this.getApplication()).setCurrentHouse(houseId);
        }
        else{
            houseId = ((MyApplication) this.getApplication()).getCurrentHouse();
        }
        setContentView(R.layout.function_menu);
        ImageButton button1 = (ImageButton) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), TempControl.class);
                startActivity(i);
                overridePendingTransition(R.anim.zoom_enter_temp,R.anim.zoom_exit_functionmenu);
            }
        });
        ImageButton button2 = (ImageButton) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Security.class);
                startActivity(i);
                overridePendingTransition(R.anim.zoom_enter_security,R.anim.zoom_exit_functionmenu);
            }
        });
        ImageButton button3 = (ImageButton) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Alarm.class);
                startActivity(i);
                overridePendingTransition(R.anim.zoom_enter_alarm,R.anim.zoom_exit_functionmenu);
            }
        });
        ImageButton button4 = (ImageButton) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Voicerec.class);
                startActivity(i);
                overridePendingTransition(R.anim.zoom_enter_voicerec,R.anim.zoom_exit_functionmenu);
            }
        });
        ImageButton button5 = (ImageButton) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), AirQuality.class);
                startActivity(i);
                overridePendingTransition(R.anim.zoom_enter_aqi,R.anim.zoom_exit_functionmenu);
            }
        });
        ImageButton button6 = (ImageButton) findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Settings.class);
                startActivity(i);
                overridePendingTransition(R.anim.zoom_enter_settings,R.anim.zoom_exit_functionmenu);
            }
        });
        if(((MyApplication)this.getApplication()).getFirstOpen()) {
            showWorkingDialog();
            UpdateValues u = new UpdateValues();
            u.run(((MyApplication) this.getApplication()));
            GetAlarmAndTemp at = new GetAlarmAndTemp();
            at.run(((MyApplication) this.getApplication()), ((MyApplication) this.getApplication()).getCurrentHouse());
            ((MyApplication)this.getApplication()).setFirstOpen(false);
            GetPostcode gpc = new GetPostcode();
            gpc.run(houseId,((MyApplication) this.getApplication()));
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkConnection();
                updateWeather();
            }
        }, 5000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                removeWorkingDialog();
            }
        }, 7000);
    }

    /*Updates weather*/
    private void updateWeather(){
        WeatherAPI wapi = new WeatherAPI();
        wapi.run(((MyApplication) this.getApplication()),((MyApplication) this.getApplication()).getPostCode());
    }

    /*Restarts app*/
    private void end(){
        ((MyApplication)this.getApplication()).setFirstOpen(true);
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(i);
    }

    /*Sets global APIKey value*/
    private void setAPIKey(){
        GetAPIKey gak = new GetAPIKey();
        if(((MyApplication) this.getApplication()).getFirstOpen()) {
            gak.run(((MyApplication)this.getApplication()),houseId);
        }
    }

    /*Checks connection status and shows alert if connection lost or starts 1 minute main loop that updates values
    * if connected successfully.
    */
    private void checkConnection(){
        System.out.println("connection lost = " + ((MyApplication) this.getApplication()).connection());
        if(((MyApplication) this.getApplication()).checkNull()){
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
            Intent startAlarm = new Intent(getApplicationContext(), AlarmReceiver.class);
            startAlarm.putExtra("HouseID",houseId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, startAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), 60000, pendingIntent);
        }
    }

    /*Initial dialog builder that runs until all http connections to retrieve values finish*/
    private void showWorkingDialog() {
        working_dialog = ProgressDialog.show(this, "","Working please wait...", true);
    }

    /*Remove working dialog*/
    private void removeWorkingDialog() {
        if (working_dialog != null) {
            working_dialog.dismiss();
            working_dialog = null;
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(a);
    }
}
