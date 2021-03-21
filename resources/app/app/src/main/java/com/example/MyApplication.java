package com.example;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {

    private boolean firstOpen = true;
    private String aqi;
    private String temperature;
    private String humidity;
    private String lock1;
    private String lock2;
    private String alarm;
    private String light1;
    private String light2;
    private String fan;
    private String targetTemp;
    private boolean intruder = false;
    private boolean connectionLost = false;
    private ArrayList<String> houseNumbers = new ArrayList<>();
    private String currentHouse;
    private String APIKey;
    private String postCode;
    private boolean validPostCode;
    private Map<String, String> weather;
    private String currentPin;

    public String getCurrentPin(){ return currentPin; }
    public String getAqi(){return aqi;}
    public String getTemperature() {
        return temperature;
    }
    public String getHumidity() {
        return humidity;
    }
    public String getLock1() {
        return lock1;
    }
    public String getLock2() {
        return lock2;
    }
    public String getAlarm() {
        return alarm;
    }
    public String getLight1() { return light1; }
    public String getLight2() { return  light2; }
    public String getTargetTemp() {return targetTemp; }
    public String getFan() {return fan; }
    public ArrayList<String> getHouseNumbers() {return houseNumbers; }
    public String getCurrentHouse() {return currentHouse; }
    public String getAPIKey(){return APIKey; }
    public boolean getIntruder() {return intruder; }
    public String getPostCode() {return postCode; }
    public Map<String, String> getWeather() {return weather; }
    public boolean validPostCode() {return validPostCode; }

    public boolean connection(){
        return connectionLost;
    }

    public boolean getFirstOpen() {
        return firstOpen;
    }

    public void setCurrentPin(String pin){this.currentPin = pin;}
    public void setAqi(String aqi){this.aqi = aqi; }
    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
    public void setLock1(String lock1){ this.lock1 = lock1; }
    public void setLock2(String lock2){ this.lock2 = lock2; }
    public void setAlarm(String alarm){ this.alarm = alarm; }
    public void setLight1(String light1){ this.light1 = light1; }
    public void setLight2(String light2){ this.light2 = light2; }
    public void setTargetTemp(String targetTemp){ this.targetTemp = targetTemp; }
    public void setHouseNumbers(ArrayList<String> houseNumbers){this.houseNumbers = houseNumbers; }
    public void setCurrentHouse(String currentHouse){this.currentHouse = currentHouse; }
    public void setIntruderF(){
        intruder = false;
    }
    public void setIntruderT(){
        intruder = true;
    }
    public void setFan(String fan) { this.fan = fan; }
    public void setAPIKey(String APIKey) { this.APIKey = APIKey; }
    public void setPostCode(String postCode) {this.postCode = postCode; }
    public void setWeather(Map<String, String> weather){this.weather = weather; }
    public void validPostCode(Boolean validPostCode){ this.validPostCode = validPostCode; }

    public void setFirstOpen(boolean firstOpen){
        this.firstOpen = firstOpen;
    }

    public void setConnectionT(){ connectionLost = true; }
    public void setConnectionF(){ connectionLost = false; }

    public boolean checkNull(){
        return temperature == null || humidity == null || lock1 == null || lock2 == null || alarm == null || light1 == null || light2 == null || aqi == null || fan == null;
    }


}
