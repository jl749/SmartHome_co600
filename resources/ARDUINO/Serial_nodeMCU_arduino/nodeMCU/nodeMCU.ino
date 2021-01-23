#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>

#include <SoftwareSerial.h>
#include <ArduinoJson.h>

const char* ssid = "";
const char* password = "";
String phpHost = "raptor.kent.ac.uk/jl749/.....";

const long interval = 5000;
unsigned long previousMillis = 0;

WiFiClient client;
HTTPClient http;
String data;

SoftwareSerial s(D6, D5);

void setup() {
  s.begin(9600);

  Serial.begin(115200); //remove after
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connecting to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
}

void loop() {
  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) { //at every interval
    previousMillis = currentMillis;
      
    if(WiFi.status()==WL_CONNECTED){
      http.begin(client,phpHost);
      http.setTimeout(1000);
      http.addHeader("Content-Type","text/plain");
      int httpCode=http.GET();
      data=http.getString();
      //getlastline(data);
      http.end();
    }
  }
  
  StaticJsonBuffer<1000> jsonBuffer;
  JsonObject& root = jsonBuffer.createObject();
  root["tmp"] = 30;
  root["intruder"] = 1;
  if (s.available() > 0)
    root.printTo(s);
}
