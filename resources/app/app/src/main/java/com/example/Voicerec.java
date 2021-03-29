package com.example;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


import static android.Manifest.permission.RECORD_AUDIO;

/**Voice recognition page class
 * This activity is called when the user navigates to the Voicerec class
 */

public class Voicerec extends Activity {

    private Intent intentRecognizer;
    private SpeechRecognizer speechRecognizer;
    private TextView status;
    private String houseID;
    private String apiKey;
    private final String errorM = "Command not recognized: ";
    private static BroadcastReceiver tickReceiver;
    private ArrayList<String> popularCommands = new ArrayList<>(
            Arrays.asList("Change temperature to 20","Turn lights off","Alarm on","Turn fan off"));


    /*
    Sets view to airquality page loads all values and sets onclick listeners
    and speech recognizer unless null values found. If null values found show
    connection lost error message.
    Sets up a loop that checks connection loss and updates ui every minute.
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voicerec_page);

        StringBuilder builder = new StringBuilder();
        for(String command:popularCommands){
            builder.append(command+"\n");
        }
        TextView popularCmds = (TextView) findViewById(R.id.popularCommands);
        popularCmds.setText(builder);

        ActivityCompat.requestPermissions(this,new String[]{RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED);

        status = findViewById(R.id.startRec);

        intentRecognizer = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {
                String errorM = "Error code: "+ error;
                status.setText(errorM);
            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                System.out.println("Word found " + matches.get(0));
                String match = "";
                if(!matches.isEmpty()){
                    match = matches.get(0);
                    CheckSpeech speech = new CheckSpeech();
                    speech.checkSpeech(match);
                    String command = speech.checkSpeech(match);
                    if(command.equals("false")){
                        status.setText(errorM + match);
                    }
                    else {
                        status.setText(match);
                        executeCommand(command);

                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        if (!((MyApplication) this.getApplication()).checkNull()) {
            houseID = ((MyApplication) this.getApplication()).getCurrentHouse();
            apiKey = ((MyApplication) this.getApplication()).getAPIKey();
            ImageButton recordVoice = (ImageButton) findViewById(R.id.recordVoice);
            recordVoice.setOnTouchListener((View v, MotionEvent event) -> {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    speechRecognizer.startListening(intentRecognizer);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    speechRecognizer.stopListening();
                }
                return false;
            });
            tickReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                        checkConnection();
                    }
                }
            };
            registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        }
        else{
            AlertDialog alertDialog = new AlertDialog.Builder(Voicerec.this).create();
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

    /*Runs connection lost alert if null values found*/
    private void checkConnection() {
        if (((MyApplication) this.getApplication()).connection()) {
            AlertDialog alertDialog = new AlertDialog.Builder(Voicerec.this).create();
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

    /*Restarts app when connection lost*/
    private void end(){
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    /*Classifies time for data mining*/
    private String classifyTime(){
        Calendar rightNow = Calendar.getInstance();
        int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY);
        if(currentHourIn24Format >= 6 && currentHourIn24Format <=10){
            return "6~10";
        }
        else if(currentHourIn24Format >= 11 && currentHourIn24Format <=13){
            return "11~13";
        }
        else if(currentHourIn24Format >= 14 && currentHourIn24Format <=17){
            return "14~17";
        }
        else if(currentHourIn24Format >= 18 && currentHourIn24Format <=22){
            return "18~22";
        }
        else{
            return "others";
        }
    }

    /*Writes a new instance every time target temp adjusted with current values*/
    private void writeInstance(String temperatureSet){
        String[] arr = new String[5];
        String weather = ((MyApplication)this.getApplication()).getTempOutside();
        arr[0] = (weather==null)?"":weather;
        arr[1] = ((MyApplication)this.getApplication()).getTemperature();
        arr[2] = temperatureSet;
        arr[3] = ((MyApplication)this.getApplication()).getHumidity();
        arr[4] = classifyTime();

        try {
            DataMining.GET().writeInstance(arr,((MyApplication) this.getApplication()).getUsername());
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /*Shows alert box to confirm action when command has been recognized*/
    private void executeCommand(String command){
        int changeTemp = tryParseInt(command,0);
        if(changeTemp!=0){
            AlertDialog alertDialog = displayConfirmationAlert("Are you sure you want to set temperature to " +changeTemp +"?");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            setTemp(changeTemp);
                            writeInstance(Integer.toString(changeTemp));
                        }
                    });
            alertDialog.show();
        }
        else if(command.equals("lightson")){
            AlertDialog alertDialog = displayConfirmationAlert("Are you sure you want to turn the lights on?");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            setLights(true);
                        }
                    });
            alertDialog.show();
        }
        else if(command.equals("lightsoff")){
            AlertDialog alertDialog = displayConfirmationAlert("Are you sure you want to turn the lights off?");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            setLights(false);
                        }
                    });
            alertDialog.show();
        }
        else if(command.equals("lockon")){
            AlertDialog alertDialog = displayConfirmationAlert("Are you sure you want to lock all doors?");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            setLock(true);
                        }
                    });
            alertDialog.show();
        }
        else if(command.equals("lockoff")){
            AlertDialog alertDialog = displayConfirmationAlert("Are you sure you want to unlock all doors?");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            setLock(false);
                        }
                    });
            alertDialog.show();
        }
        else if(command.equals("fanon")){
            AlertDialog alertDialog = displayConfirmationAlert("Are you sure you want to turn the fan on?");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            setFan(true);
                        }
                    });
            alertDialog.show();
        }
        else if(command.equals("fanoff")){
            AlertDialog alertDialog = displayConfirmationAlert("Are you sure you want to turn the fan off?");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            setFan(false);
                        }
                    });
            alertDialog.show();
        }
        else if(command.equals("alarmoff")){
            AlertDialog alertDialog = displayConfirmationAlert("Are you sure you want to turn intruder alarm off?");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            setAlarm(false);
                        }
                    });
            alertDialog.show();
        }
        else if(command.equals("alarmon")){
            AlertDialog alertDialog = displayConfirmationAlert("Are you sure you want to turn intruder alarm on?");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            setAlarm(true);
                        }
                    });
            alertDialog.show();
        }
        else {
            AlertDialog alertDialog = displayConfirmationAlert("You will be navigated to "+command);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Class<?> c = Class.forName("com.example."+command);
                                Intent intent = new Intent(Voicerec.this, c);
                                startActivity(intent);
                                if (speechRecognizer != null) {
                                    speechRecognizer.destroy();
                                }
                                finish();
                            } catch (ClassNotFoundException ignored) {
                                status.setText(errorM);
                            }
                        }
                    });
            alertDialog.show();
        }
    }

    /*Builds alert box*/
    private AlertDialog displayConfirmationAlert(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(Voicerec.this).create();
        alertDialog.setTitle("Confirm command");
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.cancel();
                    }
                });
        return alertDialog;
    }

    /*String to integer conversion*/
    private int tryParseInt(String value, int defaultVal) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /*Sets target temperature*/
    private void setTemp(int targetTemp){
        SetTemperature setTemp = new SetTemperature();
        setTemp.run(targetTemp, houseID);
        ((MyApplication)this.getApplication()).setTargetTemp(Integer.toString(targetTemp));
    }

    /*Turns LEDs on or off*/
    private void setLights(boolean state){
        final FanLedLockControl fllc = new FanLedLockControl();
        fllc.run("1", state,apiKey,"LED");
        if(state) {
            ((MyApplication)this.getApplication()).setLight1("1");
        }
        else{
            ((MyApplication)this.getApplication()).setLight1("0");
        }
    }

    /*Locks/unlocks doors*/
    private void setLock(boolean state){
        final FanLedLockControl fllc = new FanLedLockControl();
        fllc.run("1", state,apiKey,"LOCK");
        if(state) {
            ((MyApplication)this.getApplication()).setLock1("1");
        }
        else{
            ((MyApplication)this.getApplication()).setLock1("0");
        }
    }

    /*Turns fan on or off*/
    private void setFan(boolean state){
        final FanLedLockControl fllc = new FanLedLockControl();
        fllc.run("1", state,apiKey,"FAN");
        if(state) {
            ((MyApplication)this.getApplication()).setFan("1");
        }
        else{
            ((MyApplication)this.getApplication()).setFan("0");
        }
    }

    /*Turn alarm on or off*/
    private void setAlarm(boolean state) {
        SetAlarm alarm = new SetAlarm();
        alarm.run(state,houseID);
        if(state) {
            ((MyApplication)this.getApplication()).setAlarm("1");
        }
        else{
            ((MyApplication)this.getApplication()).setAlarm("0");
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
        Intent i = new Intent(Voicerec.this, FunctionMenu.class);
        startActivity(i);
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        finish();
    }
}