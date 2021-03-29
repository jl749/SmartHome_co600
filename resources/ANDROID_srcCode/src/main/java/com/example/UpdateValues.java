package com.example;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*Class called every minute that retrieves the values of sensors,lights,fans and locks from arduino
 */
public class UpdateValues{

    public void  run(MyApplication m){
        GetHttp h = new GetHttp(m);
        h.execute();
    }

    public void setVariables(ArrayList<String> array,MyApplication m){
        boolean connected = true;
        if(array.isEmpty()){
            connected = false;
            m.setConnectionT();
            System.out.println("Could not establish connection");
            System.out.println(m.connection());
        }
        if(connected) {
            m.setConnectionF();
            String aqi = array.get(0).replace("\"", "");
            String temperature = array.get(1).replace("\"", "");
            String humidity = array.get(2).replace("\"", "");
            String light1 = array.get(3).replace("\"", "");
            String light2 = array.get(4).replace("\"", "");
            String fan = array.get(5).replace("\"", "");
            String lock1 = array.get(6).replace("\"", "");
            String alarm = array.get(7).replace("\"", "");

            m.setAqi(aqi);
            m.setTemperature(temperature);
            m.setHumidity(humidity);
            m.setLight1(light1);
            m.setLight2(light2);
            m.setFan(fan);
            m.setLock1(lock1);
            m.setLock2("0");
            if(alarm.equals("0")){
                m.setIntruderF();
            }
            else if(alarm.equals("1")){
                m.setIntruderT();
            }
        }
    }

    private class GetHttp extends AsyncTask<Void, Void, Void> {
        ArrayList<String> variables = new ArrayList<String>();
        String result;
        MyApplication m;
        private static final String raptor = MainActivity.raptor;
        //private static final String raptor = MainActivity.nodMCUwebServer;
        public GetHttp(MyApplication m){
            super();
            this.m = m;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            HttpURLConnection con=null;
            URL url=null;

            try {
                url = new URL(raptor+"status.html");
                con = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                try {
                    String stringBuffer;
                    String string = "";
                    while ((stringBuffer = bufferedReader.readLine()) != null) {
                        string = String.format("%s%s", string, stringBuffer);
                    }
                    result = string;
                }finally {
                    bufferedReader.close();
                }
            }
            catch (IOException e){
                e.printStackTrace();
                result = e.toString();
            }
            finally{con.disconnect();}

            Pattern p = Pattern.compile("\"([^\"]*)\"");
            Matcher m = p.matcher(result);
            while (m.find()) variables.add(m.group());
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            setVariables(variables, m);
            System.out.println(variables);
        }
    }
}
