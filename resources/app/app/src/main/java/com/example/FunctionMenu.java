package com.example;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class FunctionMenu extends Activity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.function_menu);
        ImageButton button1 = (ImageButton) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), TempControl.class);
                startActivity(i);
            }
        });
        ImageButton button2 = (ImageButton) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Security.class);
                startActivity(i);
            }
        });
        ImageButton button3 = (ImageButton) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Alarm.class);
                startActivity(i);
            }
        });
        ImageButton button4 = (ImageButton) findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Voicerec.class);
                startActivity(i);
            }
        });
        ImageButton button5 = (ImageButton) findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Settings.class);
                startActivity(i);
            }
        });
        ImageButton button6 = (ImageButton) findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), Settings.class);
                startActivity(i);
            }
        });
        new getHttp().execute();
    }
    public void setVariables(ArrayList<String> array) {
        boolean firstOpen = ((MyApplication) this.getApplication()).getFirstOpen();
        if(firstOpen){
            String temperature = array.get(0).replace("\"", "");
            String humidity = array.get(1).replace("\"", "");
            String alarm = array.get(2).replace("\"", "");
            String light1 = array.get(3).replace("\"", "");
            String light2 = array.get(4).replace("\"", "");
            String lock1 = array.get(6).replace("\"", "");
            String lock2 = array.get(7).replace("\"", "");

            ((MyApplication) this.getApplication()).setTemperature(temperature + "°C");
            ((MyApplication) this.getApplication()).setHumidity(humidity + "%");
            ((MyApplication) this.getApplication()).setAlarm(alarm);
            ((MyApplication) this.getApplication()).setLight1(light1);
            ((MyApplication) this.getApplication()).setLight2(light2);
            ((MyApplication) this.getApplication()).setLock1(lock1);
            ((MyApplication) this.getApplication()).setLock2(lock2);

            ((MyApplication) this.getApplication()).setFirstOpen();
        }
    }


    private class getHttp extends AsyncTask<Void, Void, Void>{
        ArrayList<String> variables = new ArrayList<String>();
        String result;

        @Override
        protected Void doInBackground(Void... voids) {
            URL url;
            try {
                url = new URL("https://raptor.kent.ac.uk/~id95/status.html");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                String stringBuffer;
                String string = "";
                while ((stringBuffer = bufferedReader.readLine()) != null){
                    string = String.format("%s%s", string, stringBuffer);
                }
                bufferedReader.close();
                result = string;
            } catch (IOException e){
                e.printStackTrace();
                result = e.toString();
            }

            Pattern p = Pattern.compile("\"([^\"]*)\"");
            Matcher m = p.matcher(result);
            while (m.find()) variables.add(m.group());
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            setVariables(variables);
        }
    }
}
