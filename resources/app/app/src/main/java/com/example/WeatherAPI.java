package com.example;

import android.os.AsyncTask;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/*Class called to get the weather using postcode as the location.
*Calls first api to get location of postcode and second api to get weather at the location
* that matches the postcode
*/
public class WeatherAPI {
    private static final String apiKey = "414a72d3e6793b70712631f53c6de000";
    //http://www.geonames.org/postalcode-search.html?q=ME7+1RF&country=GB
    //http://api.openweathermap.org/data/2.5/weather?q=medway&appid=414a72d3e6793b70712631f53c6de000

    public void run(MyApplication m, String postCode){
        if(postCode!=null) {
            GetLocationAndWeather glaw = new GetLocationAndWeather(postCode, m);
            glaw.execute();
        }

    }

    private void setVariables(Map<String, String> result,MyApplication m){
        if(result==null){
            m.setWeather(null);
            m.setTempOutside("?");
        }
        else{
            System.out.println(result);
            m.setWeather(result);
            int tmp = (int) Math.round(Double.parseDouble(Objects.requireNonNull(result.get("temp"))));
            m.setTempOutside(Integer.toString(tmp));
        }

    }

    private static String removeTag(String str){
        return str.replaceAll("<td>|</td>|<tr>","").trim();
    }

    private class GetLocationAndWeather extends AsyncTask<Void, Void, Void>{

        String postCode;
        List<String> allMatches = new ArrayList<>();
        MyApplication m;

        public GetLocationAndWeather(String postCode,MyApplication m) {
            super();
            this.postCode = postCode.replaceAll(" ","+");
            this.m = m;
        }

        public InputStreamReader connectGeo(){
            InputStreamReader reader = null;
            try{
                String sURL="http://www.geonames.org/postalcode-search.html?q="+postCode+"&country=GB";
                URL url=new URL(sURL);
                HttpURLConnection request=(HttpURLConnection) url.openConnection();
                request.connect();
                reader = new InputStreamReader(request.getInputStream());
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return reader;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                m.validPostCode(true);
                //Stream<String> lines = new BufferedReader(new InputStreamReader(request.getInputStream())).lines(); //stream one time instance, closed after use
                InputStreamReader reader = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    reader = connectGeo();
                    Stream<String> lines = new BufferedReader(reader).lines(); //stream one time instance, closed after use
                    if(lines.skip(130).findFirst().get().contains("No rows found")){
                        System.out.println("not a valid postCode");
                        m.validPostCode(false);
                        allMatches = null;
                        return null;
                    }
                }
                String line147 = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    reader = connectGeo();
                    Stream<String> lines = new BufferedReader(reader).lines();
                    line147 = lines.skip(146).findFirst().get();
                }
                Matcher m = Pattern.compile("(<td>[A-Za-z, 0-9]+<\\/td>)").matcher(line147);

                while(m.find())
                    allMatches.add(removeTag(m.group()));
                m = Pattern.compile("<td>[A-Za-z, 0-9()]+<tr><td>").matcher(line147);
                while(m.find())
                    allMatches.add(removeTag(m.group().replaceAll("\\([A-Za-z 0-9]+\\)","")));

                System.out.println(allMatches);
                String yourLocation=allMatches.get(0);

                System.out.println("weather in "+yourLocation);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            System.out.println(allMatches);
            if(allMatches == null || allMatches.isEmpty()){
                setVariables(null,m);
            }
            else {
                ArrayList<String> locations = new ArrayList<>();
                locations.add(allMatches.get(0));
                for(int i= allMatches.size()-1;i>=2;i--){
                    locations.add(allMatches.get(i));
                }
                GetWeather gw = new GetWeather(locations, m);
                gw.execute();
            }
        }
    }


    private class GetWeather extends AsyncTask<Void, Void, Void> {

        ArrayList<String> locations;
        Map<String, String> result = new HashMap<>();
        String city;
        MyApplication m;

        GetWeather(ArrayList<String> locations,MyApplication m){
            super();
            this.locations = locations;
            this.city = locations.get(0).replaceAll(" ","+");
            this.m = m;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                String sURL="http://api.openweathermap.org/data/2.5/weather?q="+city+"&units=metric&appid="+apiKey;
                URL url=new URL(sURL);
                HttpURLConnection request=(HttpURLConnection) url.openConnection();
                request.connect();

                if(request.getResponseCode()<400){ //if 4nn 5nn --> .getErrorStream()
                    //JSONObject jobj=(JSONObject)(new JSONParser().parse(new InputStreamReader(request.getInputStream())) );
                    Scanner s = new Scanner(request.getInputStream()).useDelimiter("\\A");
                    String alltxt = s.hasNext() ? s.next() : "";

                    JSONTokener tokener = new JSONTokener(alltxt);
                    JSONObject jobj=new JSONObject(tokener);
                    JSONObject main=(JSONObject)jobj.get("main");
                    result.put("temp", main.get("temp").toString());
                    result.put("feels_like", main.get("feels_like").toString());
                    result.put("temp_min", main.get("temp_min").toString());
                    result.put("temp_max", main.get("temp_max").toString());
                    result.put("humidity", main.get("humidity").toString());

                    JSONObject weather = (JSONObject) ((JSONArray) jobj.get("weather")).get(0);
                    result.put("description", weather.get("description").toString());
                    result.put("icon",weather.get("icon").toString());

                    JSONObject wind = (JSONObject) jobj.get("wind");
                    result.put("speed", wind.get("speed").toString());
                    result.put("deg", wind.get("deg").toString());

                    result.put("city", jobj.get("name").toString());
                }else{
                    //JSONObject jobj=(JSONObject)(new JSONParser().parse(new InputStreamReader(request.getErrorStream())) );
                    Scanner s = new Scanner(request.getErrorStream()).useDelimiter("\\A");
                    String alltxt = s.hasNext() ? s.next() : "";
                    JSONTokener tokener = new JSONTokener(alltxt);
                    JSONObject jobj=new JSONObject(tokener);
                    System.out.println(jobj.get("message"));
                    result = null;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;

        }
        @Override
        protected void onPostExecute(Void results) {
            if(result==null) {
                if(locations.size()>1) {
                    locations.remove(0);
                    GetWeather gw = new GetWeather(locations,m);
                    gw.execute();
                }
                else{
                    setVariables(null,m);
                }
            }
            else{
                setVariables(result,m);
            }
        }
    }
}