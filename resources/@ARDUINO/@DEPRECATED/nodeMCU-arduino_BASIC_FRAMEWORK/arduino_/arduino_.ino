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

#define ledPin1 12
#define ledPin2 13

const long interval = 5000;
unsigned long previousMillis = 0;

void setup() {
  pinMode(ledPin1, OUTPUT);
  pinMode(ledPin2, OUTPUT);
  digitalWrite(ledPin1, LOW);
  digitalWrite(ledPin2, LOW);
  
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
    if(incomingString.indexOf(F("LED2/1"))!=-1){
      Serial.println(F("LED2 on"));
      digitalWrite(ledPin2, HIGH);
    }else if(incomingString.indexOf(F("LED2/0"))!=-1){
      Serial.println(F("LED2 off"));
      digitalWrite(ledPin2, LOW);
    }else if(incomingString.indexOf(F("LED1/1"))!=-1){
      Serial.println(F("LED1 on"));
      digitalWrite(ledPin1, HIGH);
    }else if(incomingString.indexOf(F("LED1/0"))!=-1){
      Serial.println(F("LED1 off"));
      digitalWrite(ledPin1, LOW);
    }
  }
  //s.print("sss");
}

void sendJSON(){
  JsonObject& root = jsonBuffer.createObject();
  root["air_quality"] = sensor.getValue();
  int chk=DHT.read11(DHT11_PIN);
  root["tmp"] = DHT.temperature;
  root["hum"] = DHT.humidity;
  root["LED1"] = (digitalRead(ledPin1)==HIGH)?0:1;
  root["LED2"] = (digitalRead(ledPin2)==HIGH)?0:1;
  //if(s.available()>0){
  root.printTo(s);
  //}
}
