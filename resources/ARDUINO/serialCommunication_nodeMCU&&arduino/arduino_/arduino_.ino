#include <SoftwareSerial.h>

#include <ArduinoJson.h>
#define JSON_RX 5
#define JSON_TX 6
SoftwareSerial s(JSON_RX,JSON_TX);
StaticJsonDocument<200> doc;

#include <dht.h>
#define DHT11_PIN 7
dht DHT;

#include "Air_Quality_Sensor.h"
AirQualitySensor sensor(A3);

#define ledPin1 12
#define ledPin2 13

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
  if(s.available()>0){
    String incomingString = s.readStringUntil('\n');
    Serial.println(incomingString);
    if(incomingString.indexOf(F("JSON"))!=-1){
      sendJSON();
    }else if(incomingString.indexOf(F("LED2/1"))!=-1){
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
}

void sendJSON(){
  doc["air_quality"] = sensor.getValue();
  int chk=DHT.read11(DHT11_PIN);
  doc["tmp"] = DHT.temperature;
  doc["hum"] = DHT.humidity;
  doc["LED1"] = (digitalRead(ledPin1)==HIGH)?true:false;
  doc["LED2"] = (digitalRead(ledPin2)==HIGH)?true:false;

  Serial.println(F("JSON sent"));
  serializeJson(doc, s);
}
