#include "WiFiEsp.h"
#include <dht.h>
#include <SoftwareSerial.h>
#include <ArduinoJson.h> //Json version5
#include "Air_Quality_Sensor.h"

#define TXPIN 2
#define RXPIN 3
SoftwareSerial esp01(TXPIN,RXPIN); //esp TX -> 2 , esp RX -> 3

#define DHT11_PIN 7
dht DHT;

#define JSON_RX 5
#define JSON_TX 6
SoftwareSerial s(JSON_RX,JSON_TX);

StaticJsonBuffer<1000> jsonBuffer;

const PROGMEM char host[]="raptor.kent.ac.uk";
const PROGMEM char ssid[]="BT-FFA25W"; //read straight from flash memory
const PROGMEM char pass[]="cATY9hbJUC4GQi";
int status=WL_IDLE_STATUS; //wifi radio's status

WiFiEspServer server(80);

AirQualitySensor sensor(A3);

#define ledPin1 12
#define ledPin2 13

#define motionS 8

char* convertPROG(PROGMEM char msg[]){
  String builder; char tmp;
  for(int i=0;i<strlen_P(msg);i++){
    tmp = pgm_read_byte_near(msg+i);
    builder.concat(tmp);}
  char* ptr=builder.c_str();
  char* arr=malloc(strlen(ptr)+1);
  strcpy(arr,ptr);
  return arr;
}

void setup() {
  pinMode(ledPin1, OUTPUT);
  pinMode(ledPin2, OUTPUT);
  pinMode(motionS, INPUT);
  digitalWrite(ledPin1, LOW);
  digitalWrite(ledPin2, LOW);
  digitalWrite(motionS, LOW);
  Serial.begin(9600);
  while(!Serial) //wait serial port to connect
    continue;

  pinMode(JSON_RX, INPUT);
  pinMode(JSON_TX, OUTPUT);
  s.begin(9600);

  Serial.println(F("Waiting sensor to init..."));
  delay(20000);
  
  esp01.begin(9600);
  WiFi.init(&esp01); //init ESP module
  while(status!=WL_CONNECTED){
    Serial.print(F("Attempting to connect ")); //F to save SRAM
    char* tmpSSID=convertPROG(ssid);
    char* tmpPASS=convertPROG(pass);
    Serial.println(tmpSSID);
    status=WiFi.begin(tmpSSID,tmpPASS); //connect
    IPAddress ip=WiFi.localIP();
    Serial.print(F("IP Address: "));
    Serial.println(ip);
  }
  server.begin();

  if (!sensor.init())
    Serial.println(F("AirS_Error"));
}

void loop() {
  motionSensor();

  
  WiFiEspClient client=server.available(); //listen incoming clients
  if(client){ //when client detected
    while(client.connected()){
      if(client.available()){ //while someting in buffer
        /**
         * when end of line('\n') is an empty line
         * http request finished
         * send response back
        */
        String income_AP=client.readStringUntil('\n');
        Serial.println(income_AP);
        if(income_AP.indexOf(F("LED2/1"))!=-1){
          Serial.println(F("LED2 on"));
          digitalWrite(ledPin2,HIGH);
        }else if(income_AP.indexOf(F("LED2/0"))!=-1){
          Serial.println(F("LED2 off"));
          digitalWrite(ledPin2,LOW);
        }
        client.flush();
        /*standard http response header*/
        client.println(F("HTTP/1.1 200 OK"));
        client.println(F("Content-Type: text/html"));
        client.println();
        client.println(F("<html>"));
        client.println(F("<body>"));
        int chk=DHT.read11(DHT11_PIN);
        client.print(F("Temperature="));
        client.print(DHT.temperature);
        client.println(F("Humidity="));
        client.print(DHT.humidity);
        int tmp=(digitalRead(ledPin2)==HIGH)?0:1;
        client.println(F("LED1="));
        client.print(tmp);
        client.println(F("LED2="));
        client.print(digitalRead(ledPin2));
        client.println(F("</body></html>"));

        delay(1000); //wait until web browser recieve all data
      }
    }
    client.stop();
    Serial.println(F("Client disconnected"));
  }
  
  delay(1000);
}

/*recieve JSON from nodeMCU*/
void jsonReceive(){
  if (s.available()>0){
   //s.read();
   JsonObject& root=jsonBuffer.parseObject(s);
   if(root!=JsonObject::invalid()){
    Serial.println(F("JSON received and parsed"));
    Serial.println(F("---------------------xxxxx--------------------"));
    root.prettyPrintTo(Serial);
    Serial.println(F("---------------------xxxxx--------------------"));
    int data1=root[F("tmp")];
    int data2=root[F("intruder")];
   }
  }
}
void motionSensor(){
   int motion=digitalRead(motionS);
   if(motion==HIGH)
     digitalWrite(ledPin1,HIGH);
   else
     digitalWrite(ledPin1,LOW);
   
}
void airSensor(){
  int quality = sensor.slope();

  Serial.print("Sensor value: ");
  Serial.println(sensor.getValue());

  if(sensor.getValue()>300){}

  if(quality == AirQualitySensor::FORCE_SIGNAL)
    Serial.println("High pollution! Force signal active.");
  else if(quality == AirQualitySensor::HIGH_POLLUTION)
    Serial.println("High pollution!");
  else if(quality == AirQualitySensor::LOW_POLLUTION)
    Serial.println("Low pollution!");
  else if(quality == AirQualitySensor::FRESH_AIR)
    Serial.println("Fresh air.");
}
