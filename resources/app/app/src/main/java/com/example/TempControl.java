package com.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TempControl extends Activity {

    private static BroadcastReceiver tickReceiver;
    final Handler handler = new Handler(Looper.getMainLooper());
    private static String houseID;
    Map<String,Integer> icons = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        houseID = ((MyApplication) this.getApplication()).getCurrentHouse();
        setContentView(R.layout.temperature_control);
        setIcons();
        if (!((MyApplication) this.getApplication()).checkNull()) {
            TextView temperature = (TextView) findViewById(R.id.CurrentTemp);
            String tempText = ((MyApplication) this.getApplication()).getTemperature() + "°C";
            temperature.setText(tempText);
            TextView humidity = (TextView) findViewById(R.id.CurrentHumid);
            humidity.setText(((MyApplication) this.getApplication()).getHumidity());
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
                    double progress = seekBar.getProgress() + 10;
                    SetTemperature setTemp = new SetTemperature();
                    setTemp.run(progress, Integer.parseInt(houseID));
                }
            });

            tickReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0) {
                        update();
                        updateWeather();
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

    public void update(){
        checkConnection();
        updateUi();
        System.out.println(((MyApplication) this.getApplication()).getTemperature());
    }

    public void checkConnection() {
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

    public void end(){
        ((MyApplication)this.getApplication()).setFirstOpen(true);
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    public void updateUi(){
        TextView temperature = (TextView) findViewById(R.id.CurrentTemp);
        TextView humidity = (TextView) findViewById(R.id.CurrentHumid);
        String temmpText = ((MyApplication) this.getApplication()).getTemperature()+ "°C";
        temperature.setText(temmpText);
        humidity.setText(((MyApplication) this.getApplication()).getHumidity());
    }

    public void updateWeather(){
        if(((MyApplication) this.getApplication()).getPostCode()!=null) {
            WeatherAPI wapi = new WeatherAPI();
            wapi.run(((MyApplication) this.getApplication()), ((MyApplication) this.getApplication()).getPostCode());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateWeatherUi();
                }
            }, 4000);
        }
    }

    public void updateWeatherUi(){
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
            //change location and image
        }
        else {
            location.setText("Invalid Postcode");
            System.out.println("INVALID POSTCODE: "+((MyApplication) this.getApplication()).getPostCode());
        }
    }


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
        Intent intent = new Intent(this, FunctionMenu.class);
        startActivity(intent);
        finish();
    }
}