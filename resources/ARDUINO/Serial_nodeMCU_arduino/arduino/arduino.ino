/*Arduino part serial*/
#include <SoftwareSerial.h>
#include <ArduinoJson.h> //Json version5

#define RX 5
#define TX 6
SoftwareSerial s(RX,TX);

void setup() {
  Serial.begin(9600);
  s.begin(9600);
  while(!Serial)
    continue;
}

void loop() {
  StaticJsonBuffer<1000> jsonBuffer;
  JsonObject& root=jsonBuffer.parseObject(s);
  if(root==JsonObject::invalid())
    return;

  Serial.println("JSON received and parsed");
  root.prettyPrintTo(Serial);
  Serial.print("Data 1 ");
  Serial.println("");
  int data1=root["tmp"];
  Serial.print(data1);
  Serial.print("   Data 2 ");
  int data2=root["intruder"];
  Serial.print(data2);
  Serial.println("");
  Serial.println("---------------------xxxxx--------------------");
}
