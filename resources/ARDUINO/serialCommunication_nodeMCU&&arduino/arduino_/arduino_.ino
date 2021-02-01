#include <SoftwareSerial.h>

#include <ArduinoJson.h>
#define JSON_RX 5
#define JSON_TX 6
SoftwareSerial s(JSON_RX,JSON_TX);
StaticJsonBuffer<1000> jsonBuffer;

#include <dht.h>
#define DHT11_PIN 7
dht DHT;

#include "Air_Quality_Sensor.h"
AirQualitySensor sensor(A3);

const long interval = 5000;
unsigned long previousMillis = 0;

void setup() {
  Serial.begin(9600);

  pinMode(JSON_RX, INPUT);
  pinMode(JSON_TX, OUTPUT);
  s.begin(9600);

  Serial.println(F("Waiting sensor to init..."));
  delay(10000);

  if (!sensor.init())
    Serial.println(F("AirS_Error"));
}

void loop() {
  unsigned long currentMillis = millis();
  if (currentMillis - previousMillis >= interval) { //at every interval
    previousMillis = currentMillis;
    sendJSON(); //send sensor values
  }
  if(s.available()>0){
    String incomingString = s.readString();
    Serial.println(incomingString);
  }
  //s.print("sss");
}

void sendJSON(){
  JsonObject& root = jsonBuffer.createObject();
  root["air_quality"] = sensor.getValue();
  int chk=DHT.read11(DHT11_PIN);
  root["tmp"] = DHT.temperature;
  root["hum"] = DHT.humidity;
  //if(s.available()>0){
  root.printTo(s);
  //}
}
