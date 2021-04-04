import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main {
    private static final String apiKey = "414a72d3e6793b70712631f53c6de000";
    //http://www.geonames.org/postalcode-search.html?q=ME7+1RF&country=GB
    //http://api.openweathermap.org/data/2.5/weather?q=medway&appid=414a72d3e6793b70712631f53c6de000

    public static Map<String, String> getCurrentW(String postCode){
        Map<String, String> result = new HashMap<>();
        postCode.replaceAll(" ","+");
        try{
            String sURL="http://www.geonames.org/postalcode-search.html?q="+postCode+"&country=GB";
            URL url=new URL(sURL);
            HttpURLConnection request=(HttpURLConnection) url.openConnection();
            request.connect();

            //Stream<String> lines = new BufferedReader(new InputStreamReader(request.getInputStream())).lines(); //stream one time instance, closed after use
            InputStreamReader reader = new InputStreamReader(request.getInputStream());
            Supplier<Stream<String>> streamSupplier = () -> new BufferedReader(reader).lines();
            if(streamSupplier.get().skip(130).findFirst().get().contains("No rows found")){
                System.out.println("not a valid postCode");
                return null;
            }
            String line147 = streamSupplier.get().skip(146).findFirst().get();
            List<String> allMatches = new ArrayList<>();
            Matcher m = Pattern.compile("(<td>[A-Za-z, 0-9]+<\\/td>)").matcher(line147);

            while(m.find())
                allMatches.add(removeTag(m.group()));
            m = Pattern.compile("<td>[A-Za-z, 0-9()]+<tr><td>").matcher(line147);
            while(m.find())
                allMatches.add(removeTag(m.group().replaceAll("\\([A-Za-z 0-9]+\\)","")));

            System.out.println(allMatches);
            String yourLocation=allMatches.get(0);
            if((result=callWeatherAPI(allMatches.get(0))) == null){
                for(int i=5;i>=3;i--)
                    if((result=callWeatherAPI(allMatches.get(i))) != null) {
                        yourLocation=allMatches.get(i);
                        break;
                    }
            }
            //result.put("yourLocation", yourLocation);

            System.out.println("weather in "+yourLocation);
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    private static String removeTag(String str){
        return str.replaceAll("<td>|</td>|<tr>","").trim();
    }

    public static Map<String, String> callWeatherAPI(String city){
        Map<String, String> result = new HashMap<>();
        city.replaceAll(" ","+");
        try{
            String sURL="http://api.openweathermap.org/data/2.5/weather?q="+city+"&units=metric&appid="+apiKey;
            URL url=new URL(sURL);
            HttpURLConnection request=(HttpURLConnection) url.openConnection();
            request.connect();

            if(request.getResponseCode()<400){ //if 4nn 5nn --> .getErrorStream()
                JSONObject jobj=(JSONObject)(new JSONParser().parse(new InputStreamReader(request.getInputStream())) );
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
                JSONObject jobj=(JSONObject)(new JSONParser().parse(new InputStreamReader(request.getErrorStream())) );
                System.out.println(jobj.get("message"));
                return null;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        //http://api.openweathermap.org/data/2.5/weather?q=kent&appid=414a72d3e6793b70712631f53c6de000
        System.out.println(getCurrentW("TD8 6HX"));
        System.out.println(getCurrentW("ME7 1RF"));
        //System.out.println(getCurrentW("null"));
    }
}
