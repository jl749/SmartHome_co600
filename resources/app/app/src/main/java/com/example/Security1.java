package com.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

/**Security (Lock and led control) page class
 * This activity is called when the user navigates to the second Security page
 */
public class Security1 extends Activity {

    float x1,x2,y1,y2;
    private static final String led = "2";
    private static BroadcastReceiver tickReceiver;
    public String apiKey;

    /*Sets view to security page loads all values and sets onclick listeners unless
    *null values found. If null values found show connection lost error message.
    *Sets up a loop that checks connection and updates ui every minute.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_page1);
        apiKey = ((MyApplication) this.getApplication()).getAPIKey();
        final FanLedLockControl fllc = new FanLedLockControl();
        update();
        updateUi();
        SwitchCompat aSwitch = findViewById(R.id.toggleLight2);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                TextView state = (TextView) findViewById(R.id.light2Status);
                if (isChecked) {
                    state.setText("Light On");
                    setLightOn();
                    fllc.run(led, true,apiKey,"LED");
                }
                else {
                    state.setText("Light Off");
                    setLightOff();
                    fllc.run(led, false,apiKey,"LED");
                }
            }
        });

        ImageButton lockButton = (ImageButton) findViewById(R.id.lockbutton1);

        lockButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextView state = (TextView) findViewById(R.id.currentState1);
                if(state.getText().equals("Currently Unlocked")){
                    ImageView image = (ImageView) findViewById(R.id.currentState);
                    image.setImageResource(R.drawable.locked);
                    state.setText("Currently Locked");
                    //call arduino to lock door
                    setClose();
                }

            }
        });

        ImageButton unlockButton = (ImageButton) findViewById(R.id.unlockbutton1);

        unlockButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextView state = (TextView) findViewById(R.id.currentState1);
                if(state.getText().equals("Currently Locked")){
                    ImageView image = (ImageView) findViewById(R.id.currentState);
                    image.setImageResource(R.drawable.unlocked);
                    state.setText("Currently Unlocked");
                    //call arduino to lock door
                    setOpen();
                }
            }
        });

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
        updateUi();
    }

    /*Runs connection lost alert if null values found*/
    private void checkConnection() {
        if (((MyApplication) this.getApplication()).connection()) {
            AlertDialog alertDialog = new AlertDialog.Builder(Security1.this).create();
            alertDialog.setTitle("Connection Error");
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setCancelable(false);
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

    /*Restarts app when connection lost*/
    private void end(){
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(i);
    }

    /*Checks arduino values and updates UI*/
    private void updateUi(){
        SwitchCompat aSwitch = findViewById(R.id.toggleLight2);
        if(((MyApplication) this.getApplication()).getLock2().equals("1")){
            TextView state = (TextView) findViewById(R.id.currentState1);
            ImageView image = (ImageView) findViewById(R.id.currentState);
            image.setImageResource(R.drawable.locked);
            state.setText("Currently Locked");
        }
        else{
            TextView state = (TextView) findViewById(R.id.currentState1);
            ImageView image = (ImageView) findViewById(R.id.currentState);
            image.setImageResource(R.drawable.unlocked);
            state.setText("Currently Unlocked");
        }

        if(((MyApplication) this.getApplication()).getLight2().equals("1")){
            aSwitch.setChecked(true);
            TextView state = (TextView) findViewById(R.id.light2Status);
            state.setText("Light On");
        }
        else {
            aSwitch.setChecked(false);
            TextView state = (TextView) findViewById(R.id.light2Status);
            state.setText("Light Off");
        }
    }

    /*Mutator for the light and lock global variables */
    private void setOpen(){
        ((MyApplication) this.getApplication()).setLock2("0");
    }
    private void setClose(){
        ((MyApplication) this.getApplication()).setLock2("1");
    }
    private void setLightOn(){
        ((MyApplication) this.getApplication()).setLight2("1");
    }
    private void setLightOff(){
        ((MyApplication) this.getApplication()).setLight2("0");
    }

    /*Checks for swipe left motion event to redirect to first security page*/
    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 < x2) {
                    Intent i = new Intent(Security1.this, Security.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                }
                break;

        }
        return false;
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
        Intent i = new Intent(Security1.this, FunctionMenu.class);
        startActivity(i);
    }
}
