#include <ESP8266WiFi.h>
const char* ssid = "BT-FFA25W";
const char* pass = "cATY9hbJUC4GQi";
const String houseID="houseID=1234";
const char* phpHost = "http://192.168.1.75/co600/getThreshold.php";
String apiKey;
WiFiClient client;

#include <ESP8266HTTPClient.h>
HTTPClient http;
WiFiServer server(80);

#include <SoftwareSerial.h>
SoftwareSerial s(D6,D5);

#include <ArduinoJson.h>
//DynamicJsonDocument doc(2048);
//StaticJsonDocument<500> doc;
int tmp,hum,air_quality;
bool led1,led2,motion,fan1,lock1=false;

int tmp_set;  bool intruder_Alarm_set,lock1_set=0;
bool ALARM_SET=0; //arduino ALARM_SET

const long interval = 5500;
unsigned long previousMillis = 0;
unsigned long count = 0;

const char* delimiter1 = "\n";
const char* delimiter2 = "=";

void setup() {
  // Initialize Serial port
  Serial.begin(9600);
  s.begin(9600);
  while(!Serial) continue;

  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, pass);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connecting to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
  server.begin();
  Serial.println("Server started");

  http.begin(F("http://192.168.1.75/co600/getAPIKey.php"));
  http.setTimeout(1000);
  http.addHeader("Content-Type","application/x-www-form-urlencoded");
  int httpCode = http.POST(houseID);
  if(httpCode > 0) {
    Serial.printf("POST code : %d\n\n", httpCode);
    if(httpCode == HTTP_CODE_OK) {
      apiKey = http.getString();
      Serial.println(apiKey);
    }
  }
  else
    Serial.printf("POST failed, error: %s\n", http.errorToString(httpCode).c_str());

  http.end();
}

void loop() {
  unsigned long currentMillis = millis();
  if(currentMillis - previousMillis >= interval){
    previousMillis = currentMillis;
    count++;
    if(s.available()==0) getJSON(); //readJSON
  }
  
  if(count >= 2){
    count=0;
    httpPOST(phpHost, houseID); //read db
    
    if(intruder_Alarm_set != ALARM_SET){
      String command = "A/";
      command += !ALARM_SET;
      s.println(command); //update alarm
    }
  }
  webServer();
}

void httpPOST(String url, String data){
  http.begin(phpHost);
  http.setTimeout(1000);
  http.addHeader("Content-Type","application/x-www-form-urlencoded");
  int httpCode = http.POST(data);
  if(httpCode > 0) {
    Serial.printf("POST code : %d\n\n", httpCode);
    if(httpCode == HTTP_CODE_OK) {
      String payload = http.getString();
      Serial.println(payload);

      char buf[payload.length()+1];
      payload.toCharArray(buf, payload.length()+1);
      
      char* end_str;
      char* token1 = strtok_r(buf, delimiter1, &end_str);
      while(token1!=NULL){
        char* end_token;
        char* token2 = strtok_r(token1, delimiter2, &end_token);
        if(strcmp(token2, "TMP_set") == 0){
          token2 = strtok_r(NULL, delimiter2, &end_token);
          tmp_set = atoi(token2);
        }else if(strcmp(token2, "Intruder_Alarm") == 0){
          token2 = strtok_r(NULL, delimiter2, &end_token);
          intruder_Alarm_set = atoi(token2);
        }else if(strcmp(token2, "Lock1") == 0){
          token2 = strtok_r(NULL, delimiter2, &end_token);
          lock1_set = atoi(token2);
        }
        token1 = strtok_r(NULL, delimiter1, &end_str);
      }
    }
  }
  else
    Serial.printf("POST failed, error: %s\n", http.errorToString(httpCode).c_str());

  http.end();
}

void webServer(){
  client = server.available();
  if(!client) return;
 
  Serial.print(F("<new client>"));
  client.setTimeout(1000);
 
  String request = client.readStringUntil('\n');
  Serial.print(F("request: "));
  Serial.println(request);

  if(request.indexOf(apiKey) != -1){
    if(request.indexOf(F("LED2/1"))!=-1){
      Serial.println(F("LED2 on"));
      s.println(F("LED2/1"));
    }else if(request.indexOf(F("LED2/0"))!=-1){
      Serial.println(F("LED2 off"));
      s.println(F("LED2/0"));
    }else if(request.indexOf(F("LED1/1"))!=-1){
      Serial.println(F("LED1 on"));
      s.println(F("LED1/1"));
    }else if(request.indexOf(F("LED1/0"))!=-1){
      Serial.println(F("LED1 off"));
      s.println(F("LED1/0"));
    }else if(request.indexOf(F("FAN1/1"))!=-1){
      Serial.println(F("FAN1 on"));
      s.println(F("FAN1/1"));
    }else if(request.indexOf(F("FAN1/0"))!=-1){
      Serial.println(F("FAN1 off"));
      s.println(F("FAN1/0"));
    }else if(request.indexOf(F("LOCK1/1"))!=-1){
      Serial.println(F("LOCK1 on"));
      s.println(F("LOCK1/1"));
    }else if(request.indexOf(F("LOCK1/0"))!=-1){
      Serial.println(F("LOCK1 off"));
      s.println(F("LOCK1/0"));
    }else if(request.indexOf(F("A_DISMISS"))!=-1){
      Serial.println(F("Alarm Dismiss"));
      motion=0;
      s.println(F("A_DISMISS"));
    }
  }
  client.flush();
 
  client.println(F("HTTP/1.1 200 OK"));
  client.println(F("Content-Type: text/html"));
  client.println();
  client.println(F("<html>"));
  client.println(F("<body>"));
  client.print(F("Air_Quality=\""));
  client.print(air_quality);
  client.print(F("\"<br>Temperature=\""));
  client.print(tmp);
  client.print(F("\"<br>Humidity=\""));
  client.print(hum);
  client.println(F("\"<br>LED1=\""));
  client.print(led1);
  client.println(F("\"<br>LED2=\""));
  client.print(led2);
  client.println(F("\"<br>FAN1=\""));
  client.print(fan1);
  client.println(F("\"<br>LOCK1=\""));
  client.print(lock1);
  client.println(F("\"<br>ALARM=\""));
  client.print(motion);
  client.println(F("\"</body></html>"));

  delay(1000); //wait until web browser recieve all data
}

void getJSON(){
  StaticJsonDocument<500> doc;
  s.println(F("JSON")); delay(300);
  
  DeserializationError err = deserializeJson(doc, s);
  if (err == DeserializationError::Ok){
    Serial.print(F("air_quality = "));
    Serial.println(air_quality = doc[0].as<double>());
    Serial.print(F("tmp = "));
    Serial.println(tmp = doc[1].as<double>());
    Serial.print(F("hum = "));
    Serial.println(hum = doc[2].as<double>());
    Serial.print(F("LED1 = "));
    Serial.println(led1 = doc[3].as<bool>());
    Serial.print(F("LED2 = "));
    Serial.println(led2 = doc[4].as<bool>());
    Serial.print(F("FAN1 = "));
    Serial.println(fan1 = doc[5].as<bool>());
    Serial.print(F("LOCK1 = "));
    Serial.println(lock1 = doc[6].as<bool>());

    Serial.print(F("motion = "));
    Serial.println(motion = doc[7].as<bool>());
    ALARM_SET = doc[8].as<bool>();
    Serial.println(F("---------------------xxxxx--------------------"));
  }else{
    Serial.print("deserializeJson() returned ");
    Serial.println(err.c_str());
    while (s.available() > 0) //empty buffer
      s.read();
  }
}
