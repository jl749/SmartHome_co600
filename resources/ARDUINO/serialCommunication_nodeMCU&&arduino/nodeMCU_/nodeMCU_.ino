#include <SoftwareSerial.h>
SoftwareSerial s(D6,D5);

#include <ArduinoJson.h>
StaticJsonBuffer<1000> jsonBuffer;

void setup() {
  // Initialize Serial port
  Serial.begin(9600);
  s.begin(9600);
  while(!Serial) continue;
}

void loop() {
  
  if(s.available()>0){ //bytes in buffer
    JsonObject& root = jsonBuffer.parseObject(s);
    if (root == JsonObject::invalid())
      return;
  
    Serial.println("JSON received and parsed");
    root.prettyPrintTo(Serial);
    Serial.println("---------------------xxxxx--------------------");
    s.println("LED1/1");
  }
}
