package com.example;

import android.app.Application;

public class MyApplication extends Application {

    private boolean firstOpen = true;
    private String temperature;
    private String humidity;
    private String lock1;
    private String lock2;
    private String alarm;
    private String light1;
    private String light2;

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

    public boolean getFirstOpen() {
        return firstOpen;
    }

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

    public void setFirstOpen(){ firstOpen = false; }
}
