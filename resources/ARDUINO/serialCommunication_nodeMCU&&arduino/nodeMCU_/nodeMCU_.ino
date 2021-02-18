#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
const char* ssid = "BT-FFA25W";
const char* pass = "cATY9hbJUC4GQi";
const char* houseID = "1234";
const char* phpHost = "http://www.cs.kent.ac.uk/people/staff/ds710/co600/getThreshold.php";

#define MOTION_PIN D4
WiFiClient client;
HTTPClient http;
WiFiServer server(80);

#include <SoftwareSerial.h>
SoftwareSerial s(D6,D5);

#include <ArduinoJson.h>
StaticJsonDocument<300> doc;
int tmp,hum,air_quality;
bool led1,led2,ALARM=false;

const long interval = 2000;
unsigned long previousMillis = 0;
unsigned char count=0;

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
}

void loop() {
  unsigned long currentMillis = millis();
  if(currentMillis - previousMillis >= interval){
    previousMillis = currentMillis;
    count++;
    if(s.available()==0) getJSON();
  }
  
  if(count >= 5){
    count=0;
    http.begin(phpHost);
    http.setTimeout(1000);
    http.addHeader("Content-Type","application/x-www-form-urlencoded");
    String httpRequestData = "houseID=";
    httpRequestData+=houseID; Serial.println(httpRequestData);
    int httpCode = http.POST(httpRequestData);
    
    if(httpCode > 0) {
      Serial.printf("POST code : %d\n\n", httpCode);
 String payload = http.getString();
        Serial.println(payload);
      if(httpCode == HTTP_CODE_OK) {
        String payload = http.getString();
        Serial.println(payload);
      }
    } 
    else
      Serial.printf("POST failed, error: %s\n", http.errorToString(httpCode).c_str());
    
    http.end();
  }
  webServer();
}

void getJSON(){
  s.println(F("JSON")); delay(3000);
  DeserializationError err = deserializeJson(doc, s);
  if (err == DeserializationError::Ok){
    //Print the values
    Serial.print(F("air_quality = "));
    Serial.println(air_quality=doc["air_quality"].as<double>());
    Serial.print(F("tmp = "));
    Serial.println(tmp=doc["tmp"].as<double>());
    Serial.print(F("hum = "));
    Serial.println(hum=doc["hum"].as<double>());
    Serial.print(F("LED1 = "));
    Serial.println(led1=doc["LED1"].as<bool>());
    Serial.print(F("LED2 = "));
    Serial.println(led2=doc["LED2"].as<bool>());
    Serial.println(F("---------------------xxxxx--------------------"));
  }else{
    // Print error to the "debug" serial port
    Serial.print("deserializeJson() returned ");
    Serial.println(err.c_str());
    while (s.available() > 0)
      s.read();
  }
}

void webServer(){
  client = server.available();
  if(!client) return;
 
  Serial.print(F("<new client>"));
  client.setTimeout(5000);
 
  String request = client.readStringUntil('\n');
  Serial.print(F("request: "));
  Serial.println(request);
 
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
  }
  client.flush();
 
  client.println(F("HTTP/1.1 200 OK"));
  client.println(F("Content-Type: text/html"));
  client.println();
  client.println(F("<html>"));
  client.println(F("<body>"));
  client.print(F("Temperature="));
  client.print(tmp);
  client.print(F("<br>Humidity="));
  client.print(hum);
  client.println(F("<br>LED1="));
  client.print(led1);
  client.println(F("<br>LED2="));
  client.print(led2);
  client.println(F("<br>ALARM="));
  client.print(ALARM);
  client.println(F("</body></html>"));

  delay(1000); //wait until web browser recieve all data
}
