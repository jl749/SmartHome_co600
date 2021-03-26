package com.example;

import java.util.ArrayList;
import java.util.Arrays;

public class CheckSpeech {

    private final ArrayList<String> tempPage = new ArrayList<String>(
            Arrays.asList("temperature", "humidity", "weather","degree","degrees"));
    private final ArrayList<String> locklightPage = new ArrayList<String>(
            Arrays.asList("light","lights","lock","security"));
    private final ArrayList<String> alarmPage = new ArrayList<String>(
            Arrays.asList("alarm","intruder"));
    private final ArrayList<String> airqualityPage = new ArrayList<String>(
            Arrays.asList("fan","air","quality"));
    private final ArrayList<String> settingsPage = new ArrayList<String>(
            Arrays.asList("settings","password"));
    private final ArrayList<String> commandWordsTemp = new ArrayList<>(
            Arrays.asList("set","change","adjust"));
    private final ArrayList<String> commandWordsAlarmOn = new ArrayList<>(
            Arrays.asList("turn alarm on","activate alarm","turn on alarm"));
    private final ArrayList<String> commandWordsAlarmOff = new ArrayList<>(
            Arrays.asList("deactivate alarm","turn alarm off","turn off alarm"));
    private final ArrayList<String> commandWordLightsOn = new ArrayList<>(
            Arrays.asList("turn lights on","turn on lights"));
    private final ArrayList<String> commandWordLightsOff = new ArrayList<>(
            Arrays.asList("turn lights off","turn off lights"));
    private final ArrayList<String> commandWordLockOn = new ArrayList<>(
            Arrays.asList("activate lock","lock"));
    private final ArrayList<String> commandWordLockOff = new ArrayList<>(
            Arrays.asList("deactivate lock","unlock","unlock door"));
    private final ArrayList<String> commandWordsFanOn = new ArrayList<>(
            Arrays.asList("turn fan on","activate fan","turn on fan"));
    private final ArrayList<String> commandWordsFanOff = new ArrayList<>(
            Arrays.asList("turn fan off","deactivate fan","turn off fan"));


    public String checkSpeech(String match){
        String command = checkCommandWord(match);

        int temperature = checkForInteger(match);

        if(command.equals("temperature") && match.contains("temperature")){
            if(temperature!=0){
                return ""+temperature;
            }
        }
        else if(command.equals("navigate")){
            return checkNavigation(match);
        }
        return command;
    }

    public int checkForInteger(String match){//check if number is in voice command
        for (String word : match.split(" ")) {
            int value = tryParseInt(word,0);
            if(value>= 15 && value <=25){
                return value;
            }
        }
        return 0;
    }

    private String checkCommandWord(String match){//check for first word to be command word
        String[] array = match.split(" ");
        for(String word: commandWordsTemp) {
            if (array[0].equals(word)) {
                return "temperature";
            }
        }
        for(String word:commandWordLightsOn){
            if(word.equals(match)){
                return "lightson";
            }
        }
        for(String word:commandWordLightsOff){
            if(word.equals(match)){
                return "lightsoff";
            }
        }
        for(String word:commandWordLockOn){
            if(word.equals(match)){
                return "lockon";
            }
        }
        for(String word:commandWordLockOff){
            if(word.equals(match)){
                return "lockoff";
            }
        }
        for(String word:commandWordsFanOn){
            if(word.equals(match)){
                return "fanon";
            }
        }
        for(String word:commandWordsFanOff){
            if(word.equals(match)){
                return "fanoff";
            }
        }
        for(String word:commandWordsAlarmOn){
            if(word.equals(match)){
                return "alarmon";
            }
        }
        for(String word:commandWordsAlarmOff){
            if(word.equals(match)){
                return "alarmoff";
            }
        }
        return "navigate";
    }

    public int tryParseInt(String value, int defaultVal) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public String checkNavigation(String match){
        for(String word: tempPage){
            if(match.contains(word)){
                return("TempControl");
            }
        }
        for(String word: locklightPage){
            if(match.contains(word)){
                return("Security");
            }
        }
        for(String word: alarmPage){
            if(match.contains(word)){
                return("Alarm");
            }
        }
        for(String word: airqualityPage){
            if(match.contains(word)){
                return("AirQuality");
            }
        }
        for(String word: settingsPage){
            if(match.contains(word)){
                return("Settings");
            }
        }
        return("false");
    }
}
