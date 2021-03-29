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
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**Temperature and weather page class
 * This activity is called when the user navigates to the temperature control page
 */

public class TempControl extends Activity {

    private static BroadcastReceiver tickReceiver;
    private static String houseID;
    private Map<String,Integer> icons = new HashMap<>();
    private String temperatureOutside;
    private String username;

    /*
    Sets view to temp control page loads all values and sets onclick listeners unless
    null values found. If null values found show connection lost error message.
    Sets up a loop that checks updates ui every minute.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        houseID = ((MyApplication) this.getApplication()).getCurrentHouse();
        username = ((MyApplication)this.getApplication()).getUsername();
        setContentView(R.layout.temperature_control);
        setIcons();
        if (!((MyApplication) this.getApplication()).checkNull()) {
            TextView temperature = (TextView) findViewById(R.id.CurrentTemp);
            String tempText = ((MyApplication) this.getApplication()).getTemperature() + "°C";
            temperature.setText(tempText);
            TextView humidity = (TextView) findViewById(R.id.CurrentHumid);
            String humidityText = ((MyApplication) this.getApplication()).getHumidity() + "%";
            humidity.setText(humidityText);
            TextView ttemp = (TextView) findViewById(R.id.TTarget);
            String ttempText = "Target Temp: " + (Integer.parseInt(((MyApplication) this.getApplication()).getTargetTemp()));
            ttemp.setText(ttempText);//get saved temp
            SeekBar tempSlider = (SeekBar) findViewById(R.id.tempSlider);
            tempSlider.setProgress(Integer.parseInt(((MyApplication) this.getApplication()).getTargetTemp())-10);//change to current
            update();
            tempSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    TextView ttemp = (TextView) findViewById(R.id.TTarget);
                    String ttempText = "Target Temp: " + (progress + 10);
                    ttemp.setText(ttempText);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int progress = seekBar.getProgress() + 10;
                    SetTemperature setTemp = new SetTemperature();
                    setTemp.run(progress, houseID);
                    writeInstance(username,Integer.toString(progress));

                }
            });

            tickReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                        update();
                        updateWeatherUi();
                    }
                }
            };
            registerReceiver(tickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
            updateWeatherUi();
        }
        else{
            AlertDialog alertDialog = new AlertDialog.Builder(TempControl.this).create();
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

    /*Writes a new instance every time target temp adjusted with current values*/
    private void writeInstance(String username,String temperatureSet){
        String[] arr = new String[5];
        arr[0] = temperatureOutside;
        arr[1] = ((MyApplication)this.getApplication()).getTemperature();
        arr[2] = temperatureSet;
        arr[3] = ((MyApplication)this.getApplication()).getHumidity();
        arr[4] = classifyTime();

        try {
            DataMining.GET().writeInstance(arr,username);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /*Checks if connection lost and updates UI (every minute)*/
    private void update(){
        checkConnection();
        updateUi();
        System.out.println(((MyApplication) this.getApplication()).getTemperature());
    }

    /*Runs connection lost alert if null values found*/
    private void checkConnection() {
        if (((MyApplication) this.getApplication()).connection()) {
            AlertDialog alertDialog = new AlertDialog.Builder(TempControl.this).create();
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
        ((MyApplication)this.getApplication()).setFirstOpen(true);
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    /*Updates Ui */
    private void updateUi(){
        TextView temperature = (TextView) findViewById(R.id.CurrentTemp);
        TextView humidity = (TextView) findViewById(R.id.CurrentHumid);
        String tempText = ((MyApplication) this.getApplication()).getTemperature()+ "°C";
        String humidityText = ((MyApplication) this.getApplication()).getHumidity()+ "%";
        temperature.setText(tempText);
        humidity.setText(humidityText);
    }

    /*Updates weather Ui */
    private void updateWeatherUi(){
        String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};
        Map<String,String> weather = ((MyApplication) this.getApplication()).getWeather();
        TextView temperature = (TextView)findViewById(R.id.WeatherTemperature);
        TextView description = (TextView)findViewById(R.id.WeatherDescription);
        TextView low = (TextView)findViewById(R.id.Low);
        TextView high = (TextView)findViewById(R.id.High);
        TextView feelsLike = (TextView)findViewById(R.id.FeelsLike);
        TextView humidity = (TextView)findViewById(R.id.Humidity);
        TextView windSpeed = (TextView)findViewById(R.id.WindSpeed);
        TextView windDirection = (TextView)findViewById(R.id.WindDirection);
        TextView location = (TextView)findViewById(R.id.City);
        ImageView weatherIcon = (ImageView)findViewById(R.id.WeatherIcon);
        if(weather!= null){
            System.out.println(weather);
            int tempTemperature = (int) Math.round(Double.parseDouble(Objects.requireNonNull(weather.get("temp"))));
            temperatureOutside = Integer.toString(tempTemperature);
            int tempHigh = (int) Math.round(Double.parseDouble(Objects.requireNonNull(weather.get("temp_max"))));
            int tempLow = (int) Math.round(Double.parseDouble(Objects.requireNonNull(weather.get("temp_min"))));
            int tempFeelsLike = (int) Math.round(Double.parseDouble(Objects.requireNonNull(weather.get("feels_like"))));
            int tempWindDirections = Integer.parseInt(Objects.requireNonNull(weather.get("deg")));
            String iconCode = weather.get("icon");
            String temperatureMessage = tempTemperature + "°C";
            String temperatureDescription = weather.get("description");
            String highMessage = tempHigh + "°C";
            String lowMessage = tempLow + "°C";
            String feelsLikeMessage = tempFeelsLike + "°C";
            String humidityMessage = weather.get("humidity") + "%";
            String windSpeedMessage = weather.get("speed") + "m/s";
            String windDirectionMessage = directions[ (int)Math.round((  ((double)tempWindDirections % 360) / 45)) ];
            String cityMessage = weather.get("city") + ", UK";
            temperature.setText(temperatureMessage);
            description.setText(temperatureDescription);
            high.setText(highMessage);
            low.setText(lowMessage);
            feelsLike.setText(feelsLikeMessage);
            humidity.setText(humidityMessage);
            windSpeed.setText(windSpeedMessage);
            windDirection.setText(windDirectionMessage);
            weatherIcon.setImageResource(icons.get(iconCode));
            location.setText(cityMessage);
        }
        else {
            temperatureOutside = "?";
            location.setText("Invalid Postcode");
            System.out.println("INVALID POSTCODE: "+((MyApplication) this.getApplication()).getPostCode());
        }
    }

    /*Maps api icon status codes to images for ui*/
    private void setIcons(){
        icons.put("01d",R.drawable.clearskyd);
        icons.put("01n",R.drawable.clearskyn);
        icons.put("02d",R.drawable.fewcloudsd);
        icons.put("02n",R.drawable.fewcloudsn);
        icons.put("03d",R.drawable.scatteredcloudsd);
        icons.put("03n",R.drawable.scatteredcloudsn);
        icons.put("04d",R.drawable.brokencloudsd);
        icons.put("04n",R.drawable.brokencloudsn);
        icons.put("09d",R.drawable.showerraind);
        icons.put("09n",R.drawable.showerrainn);
        icons.put("10d",R.drawable.raind);
        icons.put("10n",R.drawable.rainn);
        icons.put("11d", R.drawable.thunderstormd);
        icons.put("11n", R.drawable.thunderstormn);
        icons.put("50d", R.drawable.mistd);
        icons.put("50n", R.drawable.mistn);
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        //unregister broadcast receiver.
        if(tickReceiver!=null)
            unregisterReceiver(tickReceiver);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, FunctionMenu.class);
        startActivity(intent);
        finish();
    }
}
