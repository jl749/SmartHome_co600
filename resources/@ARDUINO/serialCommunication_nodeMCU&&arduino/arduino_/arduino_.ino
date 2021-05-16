#include <SoftwareSerial.h>

#include <ArduinoJson.h>
#define JSON_RX 5
#define JSON_TX 6
SoftwareSerial s(JSON_RX,JSON_TX);
//DynamicJsonDocument doc(2048);
//StaticJsonDocument<500> doc;

#include <dht.h>
#define DHT11_PIN 7
dht DHT;

#include "Air_Quality_Sensor.h"
AirQualitySensor sensor(A3);

#define ledPin1 12
#define ledPin2 13
#define MOTION_PIN 2
bool motion,ALARM_SET=0;

#define HEAT A0
#define FAN1 10
#define FAN2 11
#define LOCK1 9

#define BUZZER 3

void setup() {
  pinMode(ledPin1, OUTPUT);
  pinMode(ledPin2, OUTPUT);
  pinMode(MOTION_PIN, INPUT);
  pinMode(FAN1, OUTPUT);  digitalWrite(FAN1, HIGH);
  pinMode(LOCK1, OUTPUT); digitalWrite(LOCK1, HIGH);
  pinMode(FAN2, OUTPUT);  digitalWrite(FAN2, HIGH);
  pinMode(HEAT, OUTPUT); digitalWrite(HEAT, HIGH);
  pinMode(BUZZER, OUTPUT);
  digitalWrite(ledPin1, LOW);
  digitalWrite(ledPin2, LOW);
  //digitalWrite(HEAT, HIGH);
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
  if(ALARM_SET && digitalRead(MOTION_PIN) == HIGH){
    motion=1;
    digitalWrite(BUZZER, HIGH);
  }else if(ALARM_SET==0)
    digitalWrite(BUZZER, LOW);
  
  if(s.available()>0){
    String incomingString = s.readStringUntil('\n');
    //Serial.println(incomingString);
    if(incomingString.indexOf(F("A_DISMISS"))!=-1){
      motion=0;
      digitalWrite(BUZZER, LOW);
    }else if(incomingString.indexOf(F("JSON"))!=-1){
      sendJSON();
    }else if(incomingString.indexOf(F("A/1"))!=-1){
      Serial.println(F("ALARM_SET = 1"));
      ALARM_SET=1;
    }else if(incomingString.indexOf(F("A/0"))!=-1){
      Serial.println(F("ALARM_SET = 0"));
      ALARM_SET=0;
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
    }else if(incomingString.indexOf(F("FAN1/1"))!=-1){
      Serial.println(F("FAN1 on"));
      digitalWrite(FAN1, LOW);
    }else if(incomingString.indexOf(F("FAN1/0"))!=-1){
      Serial.println(F("FAN1 off"));
      digitalWrite(FAN1, HIGH);
    }else if(incomingString.indexOf(F("FAN2/1"))!=-1){
      Serial.println(F("FAN2 on"));
      digitalWrite(FAN2, LOW);
    }else if(incomingString.indexOf(F("FAN2/0"))!=-1){
      Serial.println(F("FAN2 off"));
      digitalWrite(FAN2, HIGH);
    }else if(incomingString.indexOf(F("LOCK1/1"))!=-1){
      Serial.println(F("LOCK1 on"));
      digitalWrite(LOCK1, LOW);
    }else if(incomingString.indexOf(F("LOCK1/0"))!=-1){
      Serial.println(F("LOCK1 off"));
      digitalWrite(LOCK1, HIGH);
    }else if(incomingString.indexOf(F("HEAT/1"))!=-1){
      Serial.println(F("HEAT on"));
      digitalWrite(HEAT, LOW);
    }else if(incomingString.indexOf(F("HEAT/0"))!=-1){
      Serial.println(F("HEAT off"));
      digitalWrite(HEAT, HIGH);
    }
  }
}

void sendJSON(){
  StaticJsonDocument<500> doc;
  int chk=DHT.read11(DHT11_PIN);
//  doc["air_quality"] = sensor.getValue();
//  doc["tmp"] = DHT.temperature;
//  doc["hum"] = DHT.humidity;
//  doc["LED1"] = (digitalRead(ledPin1)==HIGH)?true:false;
//  doc["LED2"] = (digitalRead(ledPin2)==HIGH)?true:false;
//  doc["FAN1"] = (digitalRead(FAN1)==LOW)?true:false;
//  doc["LOCK1"] = (digitalRead(LOCK1)==LOW)?true:false;
  doc.add(sensor.getValue());
  doc.add(DHT.temperature);
  doc.add(DHT.humidity);
  doc.add((digitalRead(ledPin1)==HIGH)?true:false);
  doc.add((digitalRead(ledPin2)==HIGH)?true:false);
  doc.add((digitalRead(FAN1)==LOW)?true:false);
  doc.add((digitalRead(LOCK1)==LOW)?true:false);
  
//  doc["motion"] = motion;
//  doc["ALARM_SET"] = ALARM_SET;
  doc.add(motion);
  doc.add(ALARM_SET);

  Serial.println(F("JSON sent"));
  //Serial.setTimeout(10000);
  serializeJson(doc, s);
}
